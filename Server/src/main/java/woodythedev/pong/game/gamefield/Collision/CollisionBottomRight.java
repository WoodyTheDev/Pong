package woodythedev.pong.game.gamefield.Collision;

import woodythedev.pong.game.gamefield.Gamefield;
import woodythedev.pong.game.gamefield.InGamePosition;
import woodythedev.pong.game.gamefield.Paddle;

public class CollisionBottomRight extends Collision {

	public CollisionBottomRight(Gamefield gamefield) {
		super(gamefield);
	}

	@Override
	protected InGamePosition calcIntersectionHorinzontalLine() {
		return gamefield.getBall().getMovement()
			.calcIntersectionXLine();
	}

	@Override
	protected InGamePosition calcIntersectionOtherHorinzontalLine() {
		return gamefield.getBall().getMovement()
			.calcIntersectionTopLine(gamefield.getHeight());
	}

	@Override
	protected InGamePosition calcIntersectionVerticalLine(int x) {
		return gamefield.getBall().getMovement()
			.calcIntersectionRightLine(x);
	}

	@Override
	protected Paddle getPlayerPaddle() {
		return gamefield.getPlayer2Paddle();
	}

}