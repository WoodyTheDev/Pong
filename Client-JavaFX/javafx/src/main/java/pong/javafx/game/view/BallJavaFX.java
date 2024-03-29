package pong.javafx.game.view;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import pong.javafx.game.model.Ball;

public class BallJavaFX extends Rectangle {

    private static final int BALL_SIZE_DEFAULT = 15;
    private static final Color BALL_COLOR = Color.BLACK;

    private Timeline animation;

    public BallJavaFX(Ball ball, PongSubScene pongSubScene) {
	this.setFill(BALL_COLOR);
	this.setWidth(ball != null ? ball.getWidth() : BALL_SIZE_DEFAULT);
	this.setHeight(ball != null ? ball.getWidth() : BALL_SIZE_DEFAULT);
	this.animation = new Timeline();
	animateToNewPosition(ball != null ? ball.getPosition().getX() : ((int) pongSubScene.getWidth() / 2),
		ball != null ? ball.getPosition().getY() : ((int) pongSubScene.getHeight() / 2),
		ball != null ? ball.getTimeForDistanceInSeconds() : 0.0);
    }

    public void animateToNewPosition(double newX, double newY, double timeInSeconds) {
	// Stopp der aktuellen Animation, falls sie läuft
	if (animation != null && animation.getStatus() == Animation.Status.RUNNING) {
	    animation.stop();
	}

	// Berechnung der Dauer
	Duration duration = Duration.seconds(timeInSeconds);

	// Erstellen von KeyValues für X und Y
	KeyValue keyValueX = new KeyValue(this.layoutXProperty(), newX, Interpolator.LINEAR);
	KeyValue keyValueY = new KeyValue(this.layoutYProperty(), newY, Interpolator.LINEAR);

	// Erstellen eines KeyFrames mit den KeyValues
	KeyFrame keyFrame = new KeyFrame(duration, keyValueX, keyValueY);

	// Hinzufügen des KeyFrames zur Timeline und Starten der Animation
	animation = new Timeline(keyFrame);
	animation.play();
    }

}
