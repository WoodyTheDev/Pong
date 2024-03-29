package pong.javafx.game.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Movement {

    @JsonProperty("b")
    private int b;

    @JsonProperty("m")
    private String m;

    public int getB() {
	return b;
    }

    public String getM() {
	return m;
    }

}