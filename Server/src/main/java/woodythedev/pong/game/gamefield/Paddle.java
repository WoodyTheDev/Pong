package woodythedev.pong.game.gamefield;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import woodythedev.pong.game.Game;
import woodythedev.pong.game.Game.PongElement;

@Getter
@Setter
public class Paddle {
	public enum paddleDirection {
		UP(-1),
		HALT(0),
		DOWN(1);

		public final int val;

		private paddleDirection(int val) {
			this.val = val;
		}
	}
	@JsonIgnore
	public final static int PADDLE_DEFAULT_HEIGHT = 60;
	@JsonIgnore
	public final static int PADDLE_DEFAULT_WIDTH = 20;
	@JsonIgnore
	private int heightOfGamefield;
	@JsonIgnore
	private int distanzePerTick;
	private int height;
	private int width;
	private PongElement paddleOwner;
	private InGamePosition position;
	@JsonIgnore
	private int direction = paddleDirection.HALT.val;
	@JsonIgnore
	private boolean sendPosition = false;
	@JsonIgnore
	private Game game;

	public Paddle(PongElement paddleOwner, InGamePosition position,
				  int heightOfGamefield, Game game) {
			this.height = PADDLE_DEFAULT_HEIGHT;
			this.width = PADDLE_DEFAULT_WIDTH;
			this.paddleOwner = paddleOwner;
			this.position = position;
			this.heightOfGamefield = heightOfGamefield;
			this.game = game;
			distanzePerTick = heightOfGamefield / 20;
		}	

	public void setDirection(int direction) {
		this.direction = direction;
		calcAndSendNewPosition();
		if(!sendPosition) {
			sendPosition = true;
			sendNewPositionAfter100MilliSeconds();
		}
	}

	private void sendNewPositionAfter100MilliSeconds() {
		CompletableFuture.delayedExecutor(100, TimeUnit.MILLISECONDS).execute(() -> {
  			// code here executes every 100 millisecond!
			if(sendPosition) {
				calcAndSendNewPosition();
				sendNewPositionAfter100MilliSeconds();
			}
		});
	}

	private void calcAndSendNewPosition() {
		calcNewPosition();
		game.sendPaddleMovement(this);
	}

	private void calcNewPosition() {
		if(direction == paddleDirection.DOWN.val) {
			int y = position.getY();
			if(y <= height / 2) {
				sendPosition = false;
				return;
			}
			y = y - distanzePerTick;
			position.setY(y > height / 2
							? y
							: height / 2);
		} else if(direction == paddleDirection.UP.val) {
			int y = position.getY();
			if(y >= heightOfGamefield - height / 2) {
				sendPosition = false;
				return;
			}
			y = y + distanzePerTick;
			position.setY(y < heightOfGamefield - height / 2
							? y
							: heightOfGamefield - height / 2);
		} else {
			sendPosition = false;
		}
	}
}
