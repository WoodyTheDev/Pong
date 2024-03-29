package pong.javafx.game.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Game {

    @JsonProperty("gamefield")
    private GameField gameField;

    @JsonIgnore
    private int id;

    @JsonProperty("stopGame")
    private boolean stopGame;

    @JsonProperty("player1")
    private Player player1;

    @JsonProperty("player2")
    private Player player2;

    @JsonProperty("scoresUntilWin")
    private int scoresUntilWin;

    public GameField getGameField() {
	return gameField;
    }

    public boolean isStopGame() {
	return stopGame;
    }

    public int getScoresUntilWin() {
	return scoresUntilWin;
    }

    public Player getPlayer1() {
	return player1;
    }

    public Player getPlayer2() {
	return player2;
    }

}
