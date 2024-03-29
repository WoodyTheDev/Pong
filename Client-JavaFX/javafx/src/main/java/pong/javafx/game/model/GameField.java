package pong.javafx.game.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GameField {

    @JsonProperty("ball")
    private Ball ball;

    @JsonProperty("player2Paddle")
    private Paddle player2Paddle;

    @JsonProperty("center")
    private Position center;

    @JsonProperty("width")
    private int width;

    @JsonProperty("player1Paddle")
    private Paddle player1Paddle;

    @JsonProperty("player1PaddleStartPosition")
    private Position player1PaddleStartPosition;

    @JsonProperty("scoredPlayer")
    private String scoredPlayer;

    @JsonProperty("player2PaddleStartPosition")
    private Position player2PaddleStartPosition;

    @JsonProperty("height")
    private int height;

    public Ball getBall() {
        return ball;
    }

    public Paddle getPlayer2Paddle() {
        return player2Paddle;
    }

    public Position getCenter() {
        return center;
    }

    public int getWidth() {
        return width;
    }

    public Paddle getPlayer1Paddle() {
        return player1Paddle;
    }

    public Position getPlayer1PaddleStartPosition() {
        return player1PaddleStartPosition;
    }

    public String getScoredPlayer() {
        return scoredPlayer;
    }

    public Position getPlayer2PaddleStartPosition() {
        return player2PaddleStartPosition;
    }

    public int getHeight() {
        return height;
    }


}