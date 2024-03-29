package woodythedev.pong.websocket;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.AllArgsConstructor;
import woodythedev.ErrorMessage;

@ControllerAdvice
@AllArgsConstructor
public class WebSocketExceptionHandler {
	
	private final SimpMessagingTemplate simpMessagingTemplate;

	@ExceptionHandler(WebSocketException.class)
    public void handleWebSocketException(WebSocketException ex) {
        ErrorMessage message = new ErrorMessage(
            ex.getErrorCode(),
            ex.getMessage(),
            "");
		System.out.println("WebSocketExceptionHandler");
		simpMessagingTemplate.convertAndSend("/player/error/" + ex.getPlayerId(),  message);
    }

}
