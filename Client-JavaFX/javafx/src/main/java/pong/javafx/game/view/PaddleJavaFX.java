package pong.javafx.game.view;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import lombok.Getter;
import lombok.Setter;
import pong.javafx.game.model.Paddle;

public class PaddleJavaFX extends Rectangle {
    
    private static final Color PLAYER1_COLOR = Color.TOMATO;

    private static final Color PLAYER2_COLOR = Color.BLUE;

    @Setter
    @Getter
    private boolean isActivePlayer;
    
    public PaddleJavaFX(Paddle paddle) {
	this.setFill(paddle.getPaddleOwner().equals("PLAYER1") ? PLAYER1_COLOR : PLAYER2_COLOR);
	this.setWidth(paddle.getWidth());
	this.setHeight(paddle.getHeight());
    }

}
