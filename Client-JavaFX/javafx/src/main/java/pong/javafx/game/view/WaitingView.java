package pong.javafx.game.view;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import pong.javafx.game.controller.PongGameController;
import pong.javafx.game.model.Game;
import pong.javafx.user.model.ActiveUser;

public class WaitingView extends AView {

    public static final Logger logger = Logger.getLogger(WaitingView.class);

    private Label loadingLabel;
    private Timeline animationTimeline;

    private PongGameController gameController;

    public WaitingView() throws FileNotFoundException {
	super();
	if (ActiveUser.getInstance().getUser() != null) {
	    gameController = new PongGameController();
	    gameController.setWaitingView(this);
	    gameController.initializeWebSocket();
	}
    }

    public void show() {
	this.menuStage.show();
	this.menuStage.requestFocus();
    }

    public void startGame(Game game) {
	this.menuStage.hide();
	try {
	    PongGameView pong = new PongGameView(game, gameController);
	    pong.createNewGame(this.menuStage);
	} catch (FileNotFoundException e) {
	    logger.error("File not found: " + e.getMessage());
	}
    }

    @Override
    protected List<Node> createAdditionalElements() {
	List<Node> additionalElements = new ArrayList<>();
	loadingLabel = new Label("Suche nach zweitem Spieler...");
	loadingLabel.getStyleClass().add("label-title");
	loadingLabel.setAlignment(Pos.CENTER);
	loadingLabel.setTranslateX(220);
	loadingLabel.setTranslateY(100);
	// Animation für den Lade-Text
	animationTimeline = new Timeline(new KeyFrame(Duration.seconds(0.5), e -> {
	    String text = loadingLabel.getText();
	    loadingLabel
		    .setText(text.equals("Suche nach zweitem Spieler...") ? "Suche nach zweitem Spieler" : text + ".");
	}));
	animationTimeline.setCycleCount(Animation.INDEFINITE);
	animationTimeline.play();
	additionalElements.add(loadingLabel);
	return additionalElements;
    }

    // Methode zum Ausblenden der Warteanimation
    public void hideLoadingAnimation() {
	loadingLabel.setVisible(false);
    }

    @Override
    protected void createButtons() throws FileNotFoundException {
	VBox vboxMenuButton = new VBox(10);
	vboxMenuButton.getStyleClass().add("background");
	vboxMenuButton.setAlignment(Pos.CENTER);
	vboxMenuButton.setTranslateX(0);
	vboxMenuButton.setTranslateY(100);

	MenuButton menuButton = new MenuButton("Zurück zum Haupmenü", 650, 850, MenuButton.MENU);
	menuButton.setOnAction(event -> {
	    menuStage.hide();
	    try {
		animationTimeline.stop();
		gameController.closeWebSocketConnection();
		StartView menuView = new StartView();
		menuView.changeScenes(menuStage);
	    } catch (FileNotFoundException e) {
		logger.error("File not found: " + e.getMessage());
	    }
	});

	vboxMenuButton.getChildren().add(menuButton);
	vBoxButtons.setAlignment(Pos.CENTER);
	vBoxButtons.getChildren().addAll(vboxMenuButton);
    }

}
