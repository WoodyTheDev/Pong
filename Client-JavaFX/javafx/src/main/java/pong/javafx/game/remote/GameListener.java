package pong.javafx.game.remote;

import pong.javafx.game.model.Ball;
import pong.javafx.game.model.GameScore;
import pong.javafx.game.model.Paddle;

public interface GameListener {

    void onScore(GameScore score);

    void onPaddleMovement(Paddle paddle);

    void onBallMovement(Ball ball);

}
