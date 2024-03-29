package pong.javafx.game.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Player {

    @JsonProperty("id")
    private int id;

    @JsonProperty("playername")
    private String playername;

    public int getId() {
	return id;
    }

    public String getPlayername() {
	return playername;
    }

}
