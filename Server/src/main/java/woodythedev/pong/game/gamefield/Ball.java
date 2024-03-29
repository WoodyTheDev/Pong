package woodythedev.pong.game.gamefield;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import woodythedev.pong.game.gamefield.Collision.Line;

@Getter
@Setter
@AllArgsConstructor
public class Ball {
	public final static int BALL_DEFAULT_HEIGHT = 20;
	public final static int BALL_DEFAULT_WIDTH = 20;
	private final static double SPEED_IN_DISTANCE_IN_1_SECOND = 200;
	private int height;
	private int width;
	private InGamePosition position;

	private Line movement;
	private double distance;
	private double timeForDistanceInSeconds;

	public void setDistance(InGamePosition futurePosition) {
		int x1 = position.getX();
		int y1 = position.getY();
		int x2 = futurePosition.getX();
		int y2 = futurePosition.getY();
		distance = Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
	}

	public double getTimeForDistanceInSeconds() {
		timeForDistanceInSeconds = distance / SPEED_IN_DISTANCE_IN_1_SECOND;
		return timeForDistanceInSeconds;
	}
}
