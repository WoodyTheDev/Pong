package pong.javafx.user.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.apache.log4j.Logger;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

public class RegisterDialog extends ADialog {

    public static final Logger logger = Logger.getLogger(RegisterDialog.class);

    private TextField emailField;
    private TextField usernameField;
    private TextField passwordField;

    @Getter
    private String enteredEmail;

    @Getter
    private String enteredUsername;

    @Getter
    private String enteredPassword;

    @Getter
    private File selectedImageFile;

    @Getter
    private Image selectedImage;

    @Getter
    @Setter
    private boolean isOkPressed;

    public RegisterDialog(Stage primaryStage) throws FileNotFoundException {
	super(primaryStage);
	emailField = createTextField();
	usernameField = createTextField();
	passwordField = createPasswordField();
    }

    // "email": "cat_lover@yahoo.com",
    // "playername": "PongMaster666",
    // "password": "garfield",
    // "role": "PLAYER"
    public void showAndWait() {
	Stage registerStage = createStage("Registrierung", 400, 450);

	grid.setPadding(new Insets(0, 10, 10, 10));
	grid.setVgap(5);
	grid.setHgap(10);

	Label infoLabel = createLabel("Bitte gebe hier deine Nutzerdaten ein:");
	Label emailLabel = createLabel("E-Mail:");
	Label usernameLabel = createLabel("Spielername:");
	Label passwordLabel = createLabel("Passwort:");

	HBox infoBox = new HBox();

	infoBox.getChildren().add(infoLabel);
	infoBox.setPadding(new Insets(10, 20, -10, 10));

	passwordField.setPromptText("dein Passwort");

	Button registerButton = createButton("Registrieren");
	registerButton.setOnAction(event -> {
	    setOkPressed(true);
	    enteredEmail = emailField.getText();
	    enteredUsername = usernameField.getText();
	    enteredPassword = passwordField.getText();
	    registerStage.close();
	});

	Button uploadImageButton = createButton("Bild hochladen");
	uploadImageButton.setOnAction(event -> {
	    FileChooser fileChooser = new FileChooser();
	    // Dateiformat-Einschränkungen setzen
	    fileChooser.getExtensionFilters()
		    .addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
	    File selectedFile = fileChooser.showOpenDialog(registerStage);
	    if (selectedFile != null) {
		try {
		    // FileInputStream erstellen, um die ausgewählte Datei zu lesen
		    FileInputStream fileInputStream = new FileInputStream(selectedFile);

		    // Erstellen eines JavaFX Image aus dem FileInputStream
		    selectedImage = new Image(fileInputStream, 100, 0, true, true);
		    selectedImageFile = selectedFile;

		    // Schließen des FileInputStream
		    fileInputStream.close();

		} catch (Exception e) {
		    logger.error("Failed to select user image: " + e.getMessage());
		}
	    }
	});

	VBox buttonBox = new VBox(20, uploadImageButton, registerButton);
	buttonBox.setAlignment(Pos.CENTER_RIGHT);
	buttonBox.setPadding(new Insets(10, 0, 20, 0));

	grid.add(emailLabel, 0, 1);
	grid.add(emailField, 1, 1);
	grid.add(usernameLabel, 0, 2);
	grid.add(usernameField, 1, 2);
	grid.add(passwordLabel, 0, 3);
	grid.add(passwordField, 1, 3);
	grid.add(buttonBox, 1, 4);

	VBox root = new VBox(infoBox, grid);
	root.getStylesheets().add("/stylesheet.css");
	root.getStyleClass().add("background-black");
	Scene scene = new Scene(root, 300, 250);
	registerStage.setScene(scene);
	registerStage.showAndWait(); // Warten, bis das Registrierungs-Fenster geschlossen ist
    }

}
