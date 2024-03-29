package pong.javafx.game.view;

import java.io.FileNotFoundException;
import java.util.List;

import org.apache.log4j.Logger;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pong.javafx.gamehistory.controller.GameHistoryDialogController;
import pong.javafx.user.controller.UserController;
import pong.javafx.user.model.ActiveUser;
import pong.javafx.user.model.User;

public class StartView extends AView {

    public static final Logger logger = Logger.getLogger(StartView.class);

    public StartView() throws FileNotFoundException {
	super();
    }

    @Override
    protected List<Node> createAdditionalElements() {
	return null;
    }

    @Override
    protected void createButtons() throws FileNotFoundException {
	User activeUser = ActiveUser.getInstance().getUser();
	VBox vboxGameButtons = new VBox(10);
	vboxGameButtons.getStyleClass().add("background-black");
	vboxGameButtons.setAlignment(Pos.CENTER);

	MenuButton joinGameButton = new MenuButton("Spiel beitreten", 650, 350, MenuButton.MENU);
	joinGameButton.setDisable(activeUser == null);
	joinGameButton.setOnAction(event -> {
	    menuStage.hide();
	    try {
		WaitingView waitingView = new WaitingView();
		waitingView.show();
	    } catch (FileNotFoundException e) {
		logger.error("File not found: " + e.getMessage());
	    }
	});

	MenuButton gameHistoryButton = new MenuButton("Spielhistorie", 650, 450, MenuButton.MENU);
	GameHistoryDialogController controller = new GameHistoryDialogController();
	controller.setStage(menuStage);
	gameHistoryButton.setOnAction(controller);
	gameHistoryButton.setDisable(activeUser == null);

	vboxGameButtons.getChildren().addAll(joinGameButton, gameHistoryButton);

	VBox vboxUserButtons = new VBox(10);
	vboxUserButtons.getStyleClass().add("background-black");
	vboxUserButtons.setAlignment(Pos.CENTER);

	MenuButton loginButton = new MenuButton(activeUser == null ? "Login" : "Logout", 650, 650, MenuButton.USER);
	MenuButton registerButton = new MenuButton("Registrieren", 650, 750, MenuButton.USER);
	MenuButton patchUserButton = new MenuButton("Ändern", 650, 850, MenuButton.USER);
	patchUserButton.setDisable(activeUser == null);

	vboxUserButtons.getChildren().addAll(loginButton, registerButton, patchUserButton);
	vboxUserButtons.setTranslateX(0);
	vboxUserButtons.setTranslateY(50);

	VBox vboxExitButton = new VBox(10);
	vboxExitButton.getStyleClass().add("background-black");
	vboxExitButton.setAlignment(Pos.CENTER);
	vboxExitButton.setTranslateX(0);
	vboxExitButton.setTranslateY(100);

	MenuButton exitButton = new MenuButton("Exit", 650, 950, MenuButton.EXIT);
	vboxExitButton.getChildren().add(exitButton);

	UserController userController = new UserController();
	loginButton.setOnAction(event -> {
	    try {
		if (loginButton.getText().equals("Login")) {
		    User loggedInUser = userController.loginAction(event, menuStage);
		    if (loggedInUser != null) {
			// Anmeldung erfolgreich
			joinGameButton.setDisable(false);
			gameHistoryButton.setDisable(false);
			patchUserButton.setDisable(false);
			loginButton.setText("Logout");
		    } else {
			// Anmeldung fehlgeschlagen
			loginButton.setText("Login");
		    }
		} else {
		    if (userController.logoutAction(event)) {
			joinGameButton.setDisable(true);
			gameHistoryButton.setDisable(true);
			patchUserButton.setDisable(true);
			loginButton.setText("Login");
		    }
		}
		createUpdateUserBar();
	    } catch (FileNotFoundException e) {
		logger.error("File not found: " + e.getMessage());
	    }
	});

	patchUserButton.setOnAction(event -> {
	    try {
		if (userController.patchUserAction(event, menuStage)) {
		    createUpdateUserBar();
		}
	    } catch (FileNotFoundException e) {
		logger.error("File not found: " + e.getMessage());
	    }
	});

	registerButton.setOnAction(event -> {
	    try {
		User newUser = userController.registerAction(event, menuStage);
		if (newUser != null) {
		    // Anmeldung erfolgreich
		    joinGameButton.setDisable(false);
		    gameHistoryButton.setDisable(false);
		    patchUserButton.setDisable(false);
		    loginButton.setText("Logout");
		}
	    } catch (FileNotFoundException e) {
		logger.error("File not found: " + e.getMessage());
	    }
	});

	exitButton.setOnAction(event -> {
	    menuStage.close();
	});

	vBoxButtons.setAlignment(Pos.CENTER);
	vBoxButtons.getChildren().addAll(vboxGameButtons, vboxUserButtons, vboxExitButton);
    }

    public Stage getMainStage() {
	return menuStage;
    }

    public void changeScenes(Stage previousStage) {
	previousStage.hide();
	menuStage.show();
    }

}