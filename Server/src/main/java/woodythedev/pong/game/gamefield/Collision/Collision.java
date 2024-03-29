package woodythedev.pong.game.gamefield.Collision;

import woodythedev.pong.game.gamefield.Ball;
import woodythedev.pong.game.gamefield.Gamefield;
import woodythedev.pong.game.gamefield.InGamePosition;
import woodythedev.pong.game.gamefield.Paddle;
import woodythedev.pong.game.gamefield.Collision.Line.Degree;

public abstract class Collision {

	protected Gamefield gamefield;

	public Collision(Gamefield gamefield) {
		this.gamefield = gamefield;
	}

	private boolean checkIfPaddleCollision(Paddle paddle) {
		Ball ball = gamefield.getBall();
		return ball.getPosition().getX() == paddle.getPosition().getX() &&
			ball.getPosition().getY() <= (paddle.getPosition().getY() + paddle.getHeight() / 2) &&
			ball.getPosition().getY() >= (paddle.getPosition().getY() - paddle.getHeight() / 2);
	}

	private boolean isBallOnPaddleLine(Paddle paddle) {
		Ball ball = gamefield.getBall();
		return ball.getPosition().getX() == paddle.getPosition().getX();
	}

	public InGamePosition getCollisionPosition() {
		Line movement = gamefield.getBall().getMovement();
		Paddle paddle = getPlayerPaddle();
		InGamePosition collision = calcIntersectionHorinzontalLine();

		if(collision.getX() < 0 || collision.getX() > gamefield.getWidth()) {
			int x;
			if(checkIfPaddleCollision(paddle)) {
				movement.setM(getMirroredVerticalDegree(movement.getM()));
				movement.calcB(gamefield.getBall().getPosition());
				collision = CollisionFactory.getCollision(gamefield).getCollisionPosition();
			} else {
				if(isBallOnPaddleLine(paddle)) {
					if(movement.getM().isLeftToRight()) {
						x = gamefield.getWidth();
					} else {
						x = 0;
					}
				} else {
					x = paddle.getPosition().getX();
				}
				collision = calcIntersectionVerticalLine(x);
			}
		} else {
			movement.setM(getMirroredHorinzontalDegree(movement.getM()));
		}
		return collision;
	}

	private Degree getMirroredVerticalDegree(Degree degree) {
		Degree mirroredDegree;
		switch(degree) {
			case DEGREE_18:
			mirroredDegree = Degree.DEGREE_162;
			break;
			case DEGREE_36:
			mirroredDegree = Degree.DEGREE_144;
			break;
			case DEGREE_54:
			mirroredDegree = Degree.DEGREE_126;
			break;
			case DEGREE_72:
			mirroredDegree = Degree.DEGREE_108;
			break;
			case DEGREE_108:
			mirroredDegree = Degree.DEGREE_72;
			break;
			case DEGREE_126:
			mirroredDegree = Degree.DEGREE_54;
			break;
			case DEGREE_144:
			mirroredDegree = Degree.DEGREE_36;
			break;
			case DEGREE_162:
			mirroredDegree = Degree.DEGREE_18;
			break;
			case DEGREE_198:
			mirroredDegree = Degree.DEGREE_342;
			break;
			case DEGREE_216:
			mirroredDegree = Degree.DEGREE_324;
			break;
			case DEGREE_234:
			mirroredDegree = Degree.DEGREE_306;
			break;
			case DEGREE_252:
			mirroredDegree = Degree.DEGREE_288;
			break;
			case DEGREE_288:
			mirroredDegree = Degree.DEGREE_252;
			break;
			case DEGREE_306:
			mirroredDegree = Degree.DEGREE_234;
			break;
			case DEGREE_324:
			mirroredDegree = Degree.DEGREE_216;
			break;
			case DEGREE_342:
			mirroredDegree = Degree.DEGREE_198;
			break;
			default:
			mirroredDegree = null;
		}
		return mirroredDegree;
	}

	private Degree getMirroredHorinzontalDegree(Degree degree) {
		Degree mirroredDegree;
		switch(degree) {
			case DEGREE_18:
			mirroredDegree = Degree.DEGREE_342;
			break;
			case DEGREE_36:
			mirroredDegree = Degree.DEGREE_324;
			break;
			case DEGREE_54:
			mirroredDegree = Degree.DEGREE_306;
			break;
			case DEGREE_72:
			mirroredDegree = Degree.DEGREE_288;
			break;
			case DEGREE_108:
			mirroredDegree = Degree.DEGREE_252;
			break;
			case DEGREE_126:
			mirroredDegree = Degree.DEGREE_234;
			break;
			case DEGREE_144:
			mirroredDegree = Degree.DEGREE_216;
			break;
			case DEGREE_162:
			mirroredDegree = Degree.DEGREE_198;
			break;
			case DEGREE_198:
			mirroredDegree = Degree.DEGREE_162;
			break;
			case DEGREE_216:
			mirroredDegree = Degree.DEGREE_144;
			break;
			case DEGREE_234:
			mirroredDegree = Degree.DEGREE_126;
			break;
			case DEGREE_252:
			mirroredDegree = Degree.DEGREE_108;
			break;
			case DEGREE_288:
			mirroredDegree = Degree.DEGREE_72;
			break;
			case DEGREE_306:
			mirroredDegree = Degree.DEGREE_54;
			break;
			case DEGREE_324:
			mirroredDegree = Degree.DEGREE_36;
			break;
			case DEGREE_342:
			mirroredDegree = Degree.DEGREE_18;
			break;
			default:
			mirroredDegree = null;
		}
		return mirroredDegree;
	}
	
	protected abstract InGamePosition calcIntersectionHorinzontalLine();

	protected abstract InGamePosition calcIntersectionOtherHorinzontalLine();

	protected abstract InGamePosition calcIntersectionVerticalLine(int x);

	protected abstract Paddle getPlayerPaddle();

} 
