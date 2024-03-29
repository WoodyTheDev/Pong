package pong.javafx.gamehistory.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GameRecord {

    @JsonProperty("createDate")
    private String createDate;

    @JsonProperty("player1")
    private String player1;

    @JsonProperty("player1Id")
    private String player1Id;

    @JsonProperty("player2")
    private String player2;

    @JsonProperty("player2Id")
    private String player2Id;

    @JsonProperty("gameScore")
    private GameScore gameScore;

    public static class GameScore {

	@JsonProperty("id")
	private String id;

	@JsonProperty("player1Score")
	private String player1Score;

	@JsonProperty("player2Score")
	private String player2Score;

	@JsonProperty("goalByPlayer")
	private String goalByPlayer;

	@JsonProperty("scoresUntilWin")
	private String scoresUntilWin;

	@JsonProperty("winner")
	private String winner;

	public String getPlayer1Score() {
	    return player1Score;
	}

	public String getPlayer2Score() {
	    return player2Score;
	}

	public String getWinner() {
	    return winner;
	}

	public String getScoresUntilWin() {
	    return scoresUntilWin;
	}

    }

}
