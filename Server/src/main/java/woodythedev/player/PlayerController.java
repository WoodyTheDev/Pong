package woodythedev.player;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import woodythedev.user.User;

@RestController
@RequestMapping("/api/v1/player")
@AllArgsConstructor
@Tag(name = "Player")
public class PlayerController {

	private final PlayerService playerService;

	@GetMapping
	public ResponseEntity<List<PlayerHistoryDTO>> getPlayerHistory(Principal connectedUser) {
		User user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

		return ResponseEntity.status(HttpStatus.OK)
				.body(playerService.getAllPlayerHistories(user));

	}
}
