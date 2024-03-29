package pong.javafx.game.view;

import java.io.FileNotFoundException;

import org.apache.log4j.Logger;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import pong.javafx.game.controller.PongGameController;
import pong.javafx.game.model.Ball;
import pong.javafx.game.model.Game;
import pong.javafx.game.model.GameField;
import pong.javafx.game.model.GameScore;
import pong.javafx.game.model.Paddle;
import pong.javafx.user.model.ActiveUser;

public class PongGameView {

    public static final Logger logger = Logger.getLogger(PongGameView.class);

    private Stage gameStage;
    private AnchorPane gamePane;
    private Scene gameScene;

    private Stage menuStage;

    private PongSubScene pongSubScene;
    private PaddleJavaFX paddle1;
    private PaddleJavaFX paddle2;
    private BallJavaFX ballJavaFX;

    private String player1Name;
    private String player2Name;

    private PongSubScene scoreSubScene;
    private Label paddle1Score;
    private Label paddle2Score;

    private PongSubScene endGameSubScene;

    private boolean isUpKeyPressed;
    private boolean isDownKeyPressed;
    private boolean previousUpKeyPressed = false;
    private boolean previousDownKeyPressed = false;
    private int currentPaddleDirection = 0;

    private Game game;
    private GameField gameField;

    private PongGameController controller;
    private MenuButton pongButton;

    public PongGameView(Game game, PongGameController controller) throws FileNotFoundException {
	this.game = game;
	this.gameField = game.getGameField();
	this.player1Name = game.getPlayer1().getPlayername().isEmpty() ? "Kein Name"
		: game.getPlayer1().getPlayername();
	this.player2Name = game.getPlayer2().getPlayername().isEmpty() ? "Kein Name"
		: game.getPlayer2().getPlayername();
	this.controller = controller;
	this.controller.setGameView(this);
	this.controller.setGameListener();

	gameStage = new Stage();
	gameStage.setTitle("Pong");
	gameStage.setWidth(1180);
	gameStage.setHeight(850);
	gameStage.setMaximized(false);
	gameStage.setResizable(true);
	gameStage.getIcons().add(new Image(getClass() //
		.getResource("/pong_icon.png").toString()));

	gamePane = new AnchorPane();
	gamePane.getStylesheets().add("/stylesheet.css");
	gamePane.getStyleClass().add("background-black");
	gameScene = new Scene(gamePane);
	gameStage.setScene(gameScene);
	createSubScene(gameField.getWidth(), gameField.getHeight());
	createScoreSubScene();
	createGameLabel();
	addListeners();
	createMenuButton();
    }

    private void createMenuButton() throws FileNotFoundException {
	pongButton = new MenuButton("Zurück zum Menü", 700, 700, MenuButton.MENU);
	pongButton.setOnAction(event -> {
	    controller.closeWebSocketConnection();
	    gameStage.hide();
	    try {
		StartView menuView = new StartView();
		menuView.changeScenes(gameStage);
	    } catch (FileNotFoundException e) {
		logger.error("File not found: " + e.getMessage());
	    }
	});
	gamePane.getChildren().add(pongButton);
    }

    private void checkSendMyPaddleMovement() {
	int newDirection = 0;
	if (isUpKeyPressed && !isDownKeyPressed) {
	    newDirection = 1; // Nach oben
	} else if (!isUpKeyPressed && isDownKeyPressed) {
	    newDirection = -1; // Nach unten
	} else if (!isUpKeyPressed && !isDownKeyPressed) {
	    newDirection = 0; // Keine Bewegung
	}

	if (newDirection != currentPaddleDirection) {
	    currentPaddleDirection = newDirection;
	    final int sendDirection = newDirection;
	    controller.sendPaddleDirection(sendDirection);
	}
    }

    private void addListeners() {
	gameScene.setOnKeyPressed(e -> {
	    boolean changed = false;
	    if ((e.getCode() == KeyCode.UP || e.getCode() == KeyCode.W) && !previousUpKeyPressed) {
		isUpKeyPressed = true;
		changed = true;
	    }
	    if ((e.getCode() == KeyCode.DOWN || e.getCode() == KeyCode.S) && !previousDownKeyPressed) {
		isDownKeyPressed = true;
		changed = true;
	    }
	    if (changed) {
		checkSendMyPaddleMovement();
	    }
	});

	gameScene.setOnKeyReleased(e -> {
	    boolean changed = false;
	    if (e.getCode() == KeyCode.UP || e.getCode() == KeyCode.W) {
		isUpKeyPressed = false;
		previousUpKeyPressed = false;
		changed = true;
	    }
	    if (e.getCode() == KeyCode.DOWN || e.getCode() == KeyCode.S) {
		isDownKeyPressed = false;
		previousDownKeyPressed = false;
		changed = true;
	    }
	    if (changed) {
		checkSendMyPaddleMovement();
	    }
	});
    }

    private void createGameLabel() {
	Label gameLabel = new Label(player1Name + " vs. " + player2Name);
	gameLabel.getStyleClass().add("label-title");
	gameLabel.setScaleX(1.5);
	gameLabel.setScaleY(1.5);
	gameLabel.setTextAlignment(TextAlignment.LEFT);
	gameLabel.setLayoutX(150);
	gameLabel.setLayoutY(40);
	gamePane.getChildren().addAll(gameLabel);
    }

    private void endGame(String winner) throws FileNotFoundException {
	// Ball resetten
	ballJavaFX.setLayoutX(gameField.getWidth() / 2);
	ballJavaFX.setLayoutY(gameField.getHeight() / 2);
	// Listener entfernen
	gameScene.setOnKeyPressed(null);
	gameScene.setOnKeyReleased(null);
	pongButton.setVisible(false);
	// WebSocket schließen
	controller.closeWebSocketConnection();
	createEndGameSubScene(winner);
    }

    public void updateScore(GameScore score) {
	int paddle1ScoreCount = score.getPlayer1Score();
	int paddle2ScoreCount = score.getPlayer2Score();
	paddle1Score.setText(String.valueOf(paddle1ScoreCount));
	paddle2Score.setText(String.valueOf(paddle2ScoreCount));
	if (endGameSubScene == null && score.getWinner() != null) {
	    try {
		endGame(score.getWinner());
	    } catch (FileNotFoundException e) {
		logger.error("File not found: " + e.getMessage());
	    }
	}
    }

    private void createScoreSubScene() {
	int scoresUntilWin = game.getScoresUntilWin();
	scoreSubScene = new PongSubScene(300, 200, 250, 650);

	VBox scoreVBox = new VBox(1);
	HBox scoreHBox = new HBox(5);
	Label paddle1name = new Label(paddle1.isActivePlayer() ? "X" : "");
	paddle1Score = new Label("0");
	Label dashLabel = new Label(" - ");
	Label paddle2name = new Label(paddle2.isActivePlayer() ? "X" : "");
	paddle2Score = new Label("0");

	Label winScore = new Label("Punkte um zu gewinnen: " + scoresUntilWin);

	paddle1Score.getStyleClass().add("label-style-72");
	dashLabel.getStyleClass().add("label-style-72");
	paddle2Score.getStyleClass().add("label-style-72");

	winScore.setFont(Font.font(22));
	paddle1name.setFont(Font.font(72));
	paddle2name.setFont(Font.font(72));

	scoreHBox.getChildren().addAll(paddle1name, paddle1Score, dashLabel, paddle2name, paddle2Score);
	scoreVBox.getChildren().addAll(scoreHBox, winScore);

	scoreSubScene.getPane().getStyleClass().add("background-transparent");
	scoreSubScene.getPane().getChildren().addAll(scoreVBox);
	gamePane.getChildren().add(scoreSubScene);
    }

    private void createSubScene(int width, int height) {
	// 960 x 540 (default)
	pongSubScene = new PongSubScene(width, height, 100, 100);
	paddle1 = new PaddleJavaFX(gameField.getPlayer1Paddle());
	paddle1.setLayoutX(gameField.getPlayer1PaddleStartPosition().getX() - (paddle1.getWidth() / 2));
	paddle1.setLayoutY(gameField.getPlayer1PaddleStartPosition().getY() - (paddle1.getHeight() / 2));

	ballJavaFX = new BallJavaFX(gameField.getBall(), pongSubScene);
	ballJavaFX.setLayoutX(gameField.getBall().getPosition().getX() - (ballJavaFX.getWidth() / 2));
	ballJavaFX.setLayoutY(gameField.getBall().getPosition().getY() - (ballJavaFX.getHeight() / 2));

	paddle2 = new PaddleJavaFX(gameField.getPlayer2Paddle());
	paddle2.setLayoutX(gameField.getPlayer2PaddleStartPosition().getX() - (paddle2.getWidth() / 2));
	paddle2.setLayoutY(gameField.getPlayer2PaddleStartPosition().getY() - (paddle2.getHeight() / 2));

	// Aktiven Spieler setzen
	if (ActiveUser.getInstance().getUser().getId() == game.getPlayer1().getId()) {
	    paddle1.setActivePlayer(true);
	} else {
	    paddle2.setActivePlayer(true);
	}

	pongSubScene.getPane().getChildren().addAll(ballJavaFX, paddle1, paddle2);
	gamePane.getChildren().add(pongSubScene);
    }

    public void createNewGame(Stage menuStage) {
	this.endGameSubScene = null;
	this.menuStage = menuStage;
	this.menuStage.hide();
	gameStage.show();
	gamePane.requestFocus();
    }

    private void createEndGameSubScene(String winner) throws FileNotFoundException {
	VBox vbox = new VBox(50);
	vbox.setAlignment(Pos.CENTER);
	vbox.setPrefSize(pongSubScene.getWidth(), pongSubScene.getHeight());

	Label resultLabel = new Label(
		winner.equals("PLAYER1") ? player1Name + " hat gewonnen!" : player2Name + " hat gewonnen!");
	resultLabel.getStyleClass().add("label-style");
	resultLabel.setScaleX(1.5);
	resultLabel.setScaleY(1.5);

	// 960 x 540 (default)
	endGameSubScene = new PongSubScene((int) pongSubScene.getWidth(), (int) pongSubScene.getHeight(), 100, 100);
	endGameSubScene.getPane().getStyleClass().add("background-transparent");

	MenuButton menuButton = new MenuButton("Zurück zum Menü", 0, 0, MenuButton.MENU_DARK);
	menuButton.setOnAction(event -> {
	    gameStage.hide();
	    try {
		StartView menuView = new StartView();
		menuView.changeScenes(gameStage);
	    } catch (FileNotFoundException e) {
		logger.error("File not found: " + e.getMessage());
	    }

	});

	MenuButton replayButton = new MenuButton("Neues Spiel", 0, 0, MenuButton.GAME);
	replayButton.setOnAction(event -> {
	    gameStage.hide();
	    try {
		WaitingView waitingView = new WaitingView();
		waitingView.changeScenes(gameStage);
	    } catch (FileNotFoundException e) {
		logger.error("File not found: " + e.getMessage());
	    }

	});

	vbox.getChildren().addAll(resultLabel, menuButton, replayButton);
	endGameSubScene.getPane().getChildren().addAll(vbox);
	gamePane.getChildren().add(endGameSubScene);
    }

    public void updatePaddlePosition(Paddle paddle) {
	PaddleJavaFX paddleFX = paddle.getPaddleOwner().equals("PLAYER1") ? paddle1 : paddle2;
	if (paddleFX != null) {
	    // JavaFX Rechteck Pkt links oben 0/0
	    // Server Mitte/Mitte
	    paddleFX.setLayoutY(paddle.getPosition().getY() - (paddleFX.getHeight() / 2));
	}
    }

    public void animateBallMovement(Ball ball) {
	if (ballJavaFX != null) {
	    ballJavaFX.animateToNewPosition(ball.getPosition().getX() - (ballJavaFX.getWidth() / 2),
		    ball.getPosition().getY() - (ballJavaFX.getHeight() / 2), ball.getTimeForDistanceInSeconds());
	}
    }

}