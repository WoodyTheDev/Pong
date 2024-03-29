package woodythedev.auth;

import woodythedev.token.Token;
import woodythedev.user.User;
import woodythedev.token.TokenRepository;
import woodythedev.user.UserRepository;
import woodythedev.config.JwtService;
import woodythedev.player.Player;
import woodythedev.player.PlayerRepository;
import woodythedev.token.TokenType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final UserRepository userRepository;
  private final PlayerRepository playerRepository;
  private final TokenRepository tokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  public AuthenticationResponse register(RegisterRequest request) {
    //Let's check if user already registered with us
    if(userRepository.findByEmail(request.getEmail()).isPresent()){
      throw new UserAlreadyExistException();
    }
    var user = User.builder()
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .role(request.getRole())
        .build();
    var savedUser = userRepository.save(user);
    var player = Player.builder()
        .playername(request.getPlayername())
        .email(request.getEmail())
        .user(savedUser)
        .build();
    var savedPlayer = playerRepository.save(player);
    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);
    saveUserToken(savedUser, savedPlayer, jwtToken);
    return AuthenticationResponse.builder()
        .accessToken(jwtToken)
        .refreshToken(refreshToken)
        .player(player)
        .build();
  }

  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    var user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new UserNotExistingException());
    var player = playerRepository.findByUser(user)
        .orElseThrow(() -> new PlayerNotExistingException());
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getEmail(),
            request.getPassword()
        )
    );
    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);
    revokeAllUserTokens(user);
    saveUserToken(user, player, jwtToken);
    return AuthenticationResponse.builder()
        .accessToken(jwtToken)
        .refreshToken(refreshToken)
        .player(player)
        .build();
  }

  public Authentication authenticate(String email, String password) {
    return authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            email,
            password
        )
    );
  }

  public Player authenticateAndGetPlayer(String email, String password) {
    authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(
        email,
        password
      )
    );
    var user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UserNotExistingException());
    var player = playerRepository.findByUser(user)
        .orElseThrow(() -> new PlayerNotExistingException());
    return player;
  }

  private void saveUserToken(User user, Player player, String jwtToken) {
    var token = Token.builder()
        .user(user)
        .player(player)
        .token(jwtToken)
        .tokenType(TokenType.BEARER)
        .expired(false)
        .revoked(false)
        .build();
    tokenRepository.save(token);
  }

  private void revokeAllUserTokens(User user) {
    var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
    if (validUserTokens.isEmpty())
      return;
    validUserTokens.forEach(token -> {
      token.setExpired(true);
      token.setRevoked(true);
    });
    tokenRepository.saveAll(validUserTokens);
  }

  public void refreshToken(
          HttpServletRequest request,
          HttpServletResponse response
  ) throws IOException {
    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    final String refreshToken;
    final String userEmail;
    if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
      return;
    }
    refreshToken = authHeader.substring(7);
    userEmail = jwtService.extractUsername(refreshToken);
    if (userEmail != null) {
      var user = userRepository.findByEmail(userEmail)
          .orElseThrow(() -> new UserNotExistingException());
      var player = playerRepository.findByUser(user)
          .orElseThrow(() -> new PlayerNotExistingException());
      if (jwtService.isTokenValid(refreshToken, user)) {
        var accessToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, player, accessToken);
        var authResponse = AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
      }
    }
  }
}
