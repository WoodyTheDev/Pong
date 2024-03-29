package woodythedev.pong.game.gamefield.Collision;

import woodythedev.pong.game.gamefield.Gamefield;

public class CollisionFactory {
	
	public static Collision getCollision(Gamefield gamefield) {
		Line movement = gamefield.getBall().getMovement();
			switch(movement.getM()) {
				case DEGREE_18: 
				case DEGREE_36:
				case DEGREE_54:
				case DEGREE_72:
					return new CollisionTopRight(gamefield);
				case DEGREE_108:
				case DEGREE_126:
				case DEGREE_144:
				case DEGREE_162:
					return new CollisionTopLeft(gamefield);
				case DEGREE_198:
				case DEGREE_216:
				case DEGREE_234:
				case DEGREE_252:
					return new CollisionBottomLeft(gamefield);
				case DEGREE_288:
				case DEGREE_306:
				case DEGREE_324:
				case DEGREE_342:
					return new CollisionBottomRight(gamefield);
			}
		return null;
	}
}
