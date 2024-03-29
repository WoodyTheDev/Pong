package woodythedev.pong.websocket;

import java.security.Principal;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import lombok.RequiredArgsConstructor;
import woodythedev.pong.game.GameService;
import woodythedev.user.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {
	private final GameService pongService;

	@EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
		//Principal exist if entry in Header exist with Authorization: Bearer access_token
		//Or url query param have username and password
		Principal principal = event.getUser();
		System.out.println("DISCONNECT");
		if(principal != null) {
			// throws ClassCastException when user does not exist. e.g. edited access_token
			var user = (User) ((UsernamePasswordAuthenticationToken) event.getUser()).getPrincipal();
			pongService.playerDisconnected(user.getPlayer());
		}
	}

}
