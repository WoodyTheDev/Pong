package pong.javafx.game.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Paddle {

    @JsonProperty("paddleOwner")
    private String paddleOwner;

    @JsonProperty("width")
    private int width;

    @JsonProperty("position")
    private Position position;

    @JsonProperty("height")
    private int height;

    public String getPaddleOwner() {
        return paddleOwner;
    }

    public int getWidth() {
        return width;
    }

    public Position getPosition() {
        return position;
    }

    public int getHeight() {
        return height;
    }

}
