package woodythedev.pong.game.gamefield;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import woodythedev.pong.game.Game.PongElement;
import woodythedev.pong.game.gamefield.Collision.CollisionFactory;
import woodythedev.pong.game.gamefield.Collision.Line;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Gamefield {
	private int height;
	private int width;
	private Ball ball;
	private Paddle player1Paddle;
	private Paddle player2Paddle;
	@Builder.Default
	private PongElement scoredPlayer = PongElement.NONE;

	public InGamePosition getCenter() {
		return new InGamePosition(width / 2, height / 2);
	}

	public InGamePosition getPlayer1PaddleStartPosition() {
		return new InGamePosition(ball.getWidth() * 2, height / 2);
	}

	public InGamePosition getPlayer2PaddleStartPosition() {
		return new InGamePosition(width - ball.getWidth() * 2, height / 2);
	}

	public void reset() {
		ball.setPosition(getCenter());
		ball.getMovement().randomDegree();
		player1Paddle.setPosition(getPlayer1PaddleStartPosition());
		player2Paddle.setPosition(getPlayer2PaddleStartPosition());
		scoredPlayer = PongElement.NONE;
	}

	private boolean checkIfScore() {
		if(ball.getPosition().getX() == 0) {
			scoredPlayer = PongElement.PLAYER2;
			return true;
		} else if(ball.getPosition().getX() == width) {
			scoredPlayer = PongElement.PLAYER1;
			return true;
		}
		return false;
	}

	public void calcNewBallPosition() {
		if(!checkIfScore()) {
			Line movement = ball.getMovement();
			movement.calcB(ball.getPosition());
			InGamePosition newPosition = CollisionFactory.getCollision(this).getCollisionPosition();
			ball.setDistance(newPosition);
			ball.setPosition(newPosition);
		}
	}

}
