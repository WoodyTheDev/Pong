package woodythedev.pong.websocket;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpAttributesContextHolder;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import woodythedev.pong.game.Game;
import woodythedev.pong.game.GameScore;
import woodythedev.pong.game.GameService;
import woodythedev.pong.game.gamefield.Ball;
import woodythedev.pong.game.gamefield.Paddle;
import woodythedev.user.User;

@Controller
@RequiredArgsConstructor
public class GameController {
	
	private final GameService gameService;
	private final SimpMessagingTemplate simpMessagingTemplate;

	/***************************************************************************/
	/***   Receive Messages   **************************************************/
	/***************************************************************************/
	@MessageMapping("/game")
	private void gameInit(Principal principal) {
		var user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
		gameService.addToGame(user.getPlayer());
	}

	@MessageMapping("/game/{id}/paddle")
	public void receivePaddleDirection(@DestinationVariable String id,
									   @Payload Integer paddleDirection,
									   Principal principal) {
		var user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
		gameService.setPaddleDirection(id, user.getPlayer(), paddleDirection);
	}

	@MessageMapping("/test")
	public void test(){
		System.out.println("TEST");
	}

	/***************************************************************************/
	/***   Send Messages   *****************************************************/
	/***************************************************************************/
	public void sendGameInit(String email, Game game) {
		simpMessagingTemplate.convertAndSendToUser(
			email, 
			"/init",
			game);
	}

	public void sendPaddleMovement(Integer gameId,
								   Paddle paddle) {
		simpMessagingTemplate.convertAndSend(
			"/game/" + gameId + "/paddleMovement", paddle);
	}

	public void sendNewBallPosition(Integer gameId, Ball ball) {
		simpMessagingTemplate.convertAndSend(
			"/game/" + gameId + "/ballMovement", ball);
	}

	public void sendScore(Integer gameId, GameScore gameScore) {
		simpMessagingTemplate.convertAndSend(
			"/game/" + gameId + "/score", gameScore);
	}

	/***************************************************************************/
	/***   Receive and Send Messages   *****************************************/
	/***************************************************************************/
	//works only if client sends message first
	@MessageMapping("/test/{id}")
	@SendTo("/player/{id}")
	public String testToUser(@DestinationVariable String id){
		System.out.println("TEST-User");
		return "Test";
	}


}
