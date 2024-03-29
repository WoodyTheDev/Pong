package woodythedev.pong.websocket;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import woodythedev.PongException;

@Getter
@Setter
@Builder
public class WebSocketException extends PongException {

	private int errorCode;
	private String playerId;
	private String msg;
	private final static String errorInfo = 
		"Error. Nothing will happen anymore. " + 
		"Connection should be closed but difficult to implement. ";

	public WebSocketException(String msg) {
		super(errorInfo + msg);
	}

	public WebSocketException() {
		super("");
	}

	public WebSocketException(int errorCode, String playerId, String msg) {
		super(errorInfo + msg);
		this.errorCode = errorCode;
		this.playerId = playerId;
		this.msg = msg;
	}

}
