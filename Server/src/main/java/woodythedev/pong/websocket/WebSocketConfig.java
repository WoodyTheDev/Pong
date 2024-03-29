package woodythedev.pong.websocket;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import lombok.RequiredArgsConstructor;
import woodythedev.auth.AuthenticationService;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
	
	private final AuthenticationService authenticationService;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws").setAllowedOriginPatterns("*").setHandshakeHandler(new CustomHandshakeHandler());
		registry.addEndpoint("/ws").setAllowedOriginPatterns("*").setHandshakeHandler(new CustomHandshakeHandler()).withSockJS();
    }

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.setApplicationDestinationPrefixes("/pong");
		config.enableSimpleBroker("/game");
		config.setUserDestinationPrefix("/game");
	}

	class CustomHandshakeHandler extends DefaultHandshakeHandler {
		// Custom class for storing principal
		@Override
		protected Principal determineUser(
			ServerHttpRequest request,
			WebSocketHandler wsHandler,
			Map<String, Object> attributes
		) {
			try {
				if(request.getURI().getQuery() != null) {
					Map<String, List<String>> queryParams = splitQuery(request.getURI());
					String email = queryParams.get("email").get(0);
					String password = queryParams.get("password").get(0);
					return authenticationService.authenticate(email, password);
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			return request.getPrincipal();
		}

		public static Map<String, List<String>> splitQuery(URI uri) throws UnsupportedEncodingException {
		final Map<String, List<String>> query_pairs = new LinkedHashMap<String, List<String>>();
		final String[] pairs = uri.getQuery().split("&");
		for (String pair : pairs) {
			final int idx = pair.indexOf("=");
			final String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
			if (!query_pairs.containsKey(key)) {
			query_pairs.put(key, new LinkedList<String>());
			}
			final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : null;
			query_pairs.get(key).add(value);
		}
		return query_pairs;
		}
	}
}

