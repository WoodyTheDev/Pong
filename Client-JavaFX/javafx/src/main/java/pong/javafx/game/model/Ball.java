package pong.javafx.game.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Ball {

    @JsonProperty("distance")
    private int distance;

    @JsonProperty("width")
    private int width;

    @JsonProperty("timeForDistanceInSeconds")
    private double timeForDistanceInSeconds;

    @JsonProperty("position")
    private Position position;

    @JsonProperty("movement")
    private Movement movement;

    @JsonProperty("height")
    private int height;

    public int getDistance() {
        return distance;
    }

    public int getWidth() {
        return width;
    }

    public double getTimeForDistanceInSeconds() {
        return timeForDistanceInSeconds;
    }

    public Position getPosition() {
        return position;
    }

    public Movement getMovement() {
        return movement;
    }

    public int getHeight() {
        return height;
    }

}
