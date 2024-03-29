package pong.javafx.game.view;

import java.io.FileNotFoundException;
import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pong.javafx.user.controller.UserController;

public abstract class AView {

    protected Stage menuStage;
    protected VBox vBoxButtons;
    private VBox vBox;
    private HBox userBox;
    private HBox hBox;
    private Scene menuScene;

    private UserController userController = new UserController();

    public AView() throws FileNotFoundException {
	userController.initialize();

	menuStage = new Stage();
	menuStage.setTitle("Pong");
	menuStage.setWidth(1400);
	menuStage.setHeight(1050);
	menuStage.setMaximized(false);
	menuStage.setResizable(false);
	menuStage.getIcons().add(new Image(getClass() //
		.getResource("/pong_icon.png").toString()));

	userBox = new HBox(10);
	userBox.setAlignment(Pos.TOP_LEFT);
	userBox.setPadding(new Insets(50, 0, 100, 50));
	userBox.setMinHeight(300);
	userBox.setVisible(true);

	vBoxButtons = new VBox(10);
	vBoxButtons.setAlignment(Pos.CENTER);

	vBox = new VBox(10);
	vBox.setAlignment(Pos.TOP_LEFT);

	hBox = new HBox(10);
	hBox.setAlignment(Pos.CENTER);

	menuScene = new Scene(vBox, 1400, 1050);
	menuScene.getStylesheets().add("/stylesheet.css");
	menuStage.setScene(menuScene);

	userBox.getStyleClass().add("background-black");
	vBoxButtons.getStyleClass().add("background-black");
	vBox.getStyleClass().add("background-black");
	hBox.getStyleClass().add("background-black");

	createButtons();
	createUpdateUserBar();

	Label titleLabel = createTitle();

	List<Node> additionalElements = createAdditionalElements();

	HBox titleAndVBoxhBox = new HBox(300);
	titleAndVBoxhBox.setAlignment(Pos.CENTER);
	titleAndVBoxhBox.getChildren().addAll(titleLabel, vBoxButtons);
	hBox.getChildren().add(titleAndVBoxhBox);
	vBox.getChildren().addAll(userBox, hBox);
	if (additionalElements != null && !additionalElements.isEmpty()) {
	    vBox.getChildren().addAll(additionalElements);
	}
    }

    protected abstract List<Node> createAdditionalElements();

    protected abstract void createButtons() throws FileNotFoundException;

    protected void createUpdateUserBar() {
	userBox.getChildren().clear();

	// Label und ImageView für Benutzerdaten
	Label userLabel = new Label("Eingeloggt als:");
	userLabel.getStyleClass().add("label-title");
	userLabel.visibleProperty().bind(userController.userNameProperty().isNotNull());

	Label userName = new Label();
	userName.getStyleClass().add("label-title");
	userName.textProperty().bind(userController.userNameProperty());

	ImageView imageView = new ImageView();
	imageView.setPreserveRatio(true);
	imageView.imageProperty().bind(userController.userImageProperty());

	HBox hBoxBorder = new HBox();
	hBoxBorder.getChildren().add(imageView);
	hBoxBorder.getStyleClass().add("image-box");
	hBoxBorder.setMaxHeight(imageView.getImage() != null ? imageView.getImage().getHeight() : 0);
	hBoxBorder.visibleProperty().bind(userController.userImageProperty().isNotNull());

	userController.updateActiveUserUI();

	userBox.getChildren().addAll(userLabel, userName, hBoxBorder);
	userBox.visibleProperty().bind(userController.userNameProperty().isNotNull());
    }

    private Label createTitle() {
	Label titleLabel = new Label("PONG");
	titleLabel.getStyleClass().add("label-title");
	titleLabel.setScaleX(5);
	titleLabel.setScaleY(5);
	return titleLabel;
    }

    public Stage getMenuStage() {
	return menuStage;
    }

    public void changeScenes(Stage previousStage) {
	previousStage.hide();
	menuStage.show();
    }

}
