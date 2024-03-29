package pong.javafx.game.controller;

import javafx.application.Platform;
import lombok.Setter;
import pong.javafx.game.model.Ball;
import pong.javafx.game.model.Game;
import pong.javafx.game.model.GameScore;
import pong.javafx.game.model.Paddle;
import pong.javafx.game.remote.GameListener;
import pong.javafx.game.remote.GameStartListener;
import pong.javafx.game.remote.PongWebSocketClient;
import pong.javafx.game.view.PongGameView;
import pong.javafx.game.view.WaitingView;
import pong.javafx.user.model.ActiveUser;

public class PongGameController implements GameListener, GameStartListener {

    @Setter
    private PongGameView gameView;

    @Setter
    private WaitingView waitingView;
    
    private PongWebSocketClient webSocketClient;

    /** WaitingView - before Game **/
    public void initializeWebSocket() {
	new Thread(() -> {
	    if (ActiveUser.getInstance().getUser().getBearerToken() == null) {
		System.err.println("Bearer Token is NULL.");
		return;
	    }
	    this.webSocketClient = new PongWebSocketClient(
		    ActiveUser.getInstance().getUser().getBearerToken().getValue());
	    webSocketClient.setGameStartListener(this);
	    webSocketClient.start();
	}).start();
    }

    public void closeWebSocketConnection() {
	webSocketClient.closeConnection();
    }

    @Override
    public void onGameStart(Game game) {
	Platform.runLater(() -> {
	    waitingView.startGame(game);
	});
    }
    
    /** PongGameView - active Game **/
    public void setGameListener() {
	webSocketClient.getWebSocketExecutor().execute(() -> webSocketClient.setGameListener(this));
    }

    public void sendPaddleDirection(int sendDirection) {
	webSocketClient.getWebSocketExecutor()
		.execute(() -> this.webSocketClient.sendPaddleDirection(Integer.toString(sendDirection)));
    }

    @Override
    public void onScore(GameScore score) {
	Platform.runLater(() -> {
	    gameView.updateScore(score);
	});
    }

    @Override
    public void onPaddleMovement(Paddle paddle) {
	Platform.runLater(() -> {
	    gameView.updatePaddlePosition(paddle);
	});
    }

    @Override
    public void onBallMovement(Ball ball) {
	Platform.runLater(() -> {
	    gameView.animateBallMovement(ball);
	});
    }
}
