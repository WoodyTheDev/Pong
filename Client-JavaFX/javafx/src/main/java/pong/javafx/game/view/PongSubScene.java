package pong.javafx.game.view;

import javafx.scene.SubScene;
import javafx.scene.layout.AnchorPane;

public class PongSubScene extends SubScene {
    public PongSubScene(int width, int height, int x, int y) {
	super(new AnchorPane(), width, height);
	this.prefWidth(width);
	this.prefHeight(height);
	this.setLayoutX(x);
	this.setLayoutY(y);

	AnchorPane root = (AnchorPane) this.getRoot();
	root.getStylesheets().add("/stylesheet.css");
	root.getStyleClass().add("background-subscene");
    }

    public AnchorPane getPane() {
	return (AnchorPane) this.getRoot();
    }

}