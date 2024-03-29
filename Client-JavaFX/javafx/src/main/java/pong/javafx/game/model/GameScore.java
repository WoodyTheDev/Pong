package pong.javafx.game.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GameScore {

    @JsonProperty("goalByPlayer")
    private Player goalByPlayer;

    @JsonProperty("winner")
    private String winner;

    @JsonProperty("player1Score")
    private int player1Score;

    @JsonProperty("player2Score")
    private int player2Score;

    @JsonIgnore
    private int id;

    @JsonProperty("scoresUntilWin")
    private int scoresUntilWin;

    public Player getGoalByPlayer() {
        return goalByPlayer;
    }

    public String getWinner() {
        return winner;
    }

    public int getPlayer1Score() {
        return player1Score;
    }

    public int getPlayer2Score() {
        return player2Score;
    }

    public int getId() {
        return id;
    }

    public int getScoresUntilWin() {
        return scoresUntilWin;
    }

}
