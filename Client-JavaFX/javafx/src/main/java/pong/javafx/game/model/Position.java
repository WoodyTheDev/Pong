package pong.javafx.game.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Position {

    @JsonProperty("x")
    private int x;

    @JsonProperty("y")
    private int y;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

}