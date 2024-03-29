package pong.javafx.user.view;

import java.io.FileNotFoundException;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

public class LoginDialog extends ADialog {

    private TextField usernameField;
    private PasswordField passwordField;

    @Getter
    private String enteredUsername;

    @Getter
    private String enteredPassword;

    @Getter
    @Setter
    private boolean isOkPressed;

    public LoginDialog(Stage primaryStage) throws FileNotFoundException {
	super(primaryStage);
	usernameField = createTextField();
	passwordField = createPasswordField();
    }

    public void showAndWait() {
	Stage loginStage = createStage("Login", 425, 280);
	loginStage.initModality(Modality.WINDOW_MODAL);
	
	grid.setPadding(new Insets(20, 10, 20, 10));
	grid.setVgap(10);
	grid.setHgap(10);

	Label usernameLabel = createLabel("E-Mail:");
	GridPane.setHgrow(usernameLabel, Priority.ALWAYS);

	Label passwordLabel = createLabel("Passwort:");

	Button loginButton = createButton("Login");
	loginButton.setOnAction(event -> {
	    setOkPressed(true);
	    enteredUsername = usernameField.getText();
	    enteredPassword = passwordField.getText();
	    loginStage.close();
	});

	// Event für Enter-Taste
	EventHandler<KeyEvent> enterPressedHandler = e -> {
	    if (e.getCode() == KeyCode.ENTER) {
		loginButton.fire();
	    }
	};
	usernameField.setOnKeyPressed(enterPressedHandler);
	passwordField.setOnKeyPressed(enterPressedHandler);

	VBox buttonBox = new VBox(20, loginButton);
	buttonBox.setAlignment(Pos.CENTER_RIGHT);
	buttonBox.setPadding(new Insets(0, 0, 20, 0));

	grid.add(usernameLabel, 0, 0);
	grid.add(usernameField, 1, 0);
	grid.add(passwordLabel, 0, 1);
	grid.add(passwordField, 1, 1);
	grid.add(buttonBox, 1, 2);

	Scene scene = new Scene(grid, 300, 150);
	loginStage.setScene(scene);
	loginStage.showAndWait(); // Warten, bis das Login-Fenster geschlossen ist
    }

}
