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
import pong.javafx.user.model.ActiveUser;

public class PatchUserDialog extends ADialog {

    public static final Logger logger = Logger.getLogger(PatchUserDialog.class);

    private TextField currentPasswordField;
    private TextField newPasswordField;
    private TextField confirmationPasswordField;

    @Getter
    private String enteredCurrentPassword;
    
    @Getter
    private String enteredNewPassword;
    
    @Getter
    private String enteredConfirmationPassword;

    @Getter
    private File selectedImageFile;

    @Getter
    private Image selectedImage;

    @Getter
    @Setter
    private boolean isOkPressed;

    public PatchUserDialog(Stage primaryStage) throws FileNotFoundException {
	super(primaryStage);
	currentPasswordField = createPasswordField();
	newPasswordField = createPasswordField();
	confirmationPasswordField = createPasswordField();
    }

    public void showAndWait() {
	Stage patchUserStage = createStage("Spielerprofil bearbeiten", 460, 500);

	grid.setPadding(new Insets(0, 10, 10, 10));
	grid.setVgap(5);
	grid.setHgap(10);

	// Labels
	Label infoLabel = createLabel("Hier kannst du deine Nutzerdaten ändern:\nPasswort und/oder das Bild lassen sich ändern.");
	Label usernameLabel = createLabel("Spielername:");
	Label username = createLabel(ActiveUser.getInstance().getUser().getUserName().getValue());
	Label currentPasswordLabel = createLabel("Aktuelles Passwort:");
	Label newPasswordLabel = createLabel("Neues Passwort:");
	Label confirmationPasswordLabel = createLabel("Passwort bestätigen:");

	HBox infoBox = new HBox();
	infoBox.getChildren().add(infoLabel);
	infoBox.setPadding(new Insets(10, 20, 0, 10));

	currentPasswordField.setPromptText("aktuelles Passwort");
	newPasswordField.setPromptText("neues Passwort");
	confirmationPasswordField.setPromptText("Passwort bestätigen");

	Button changeButton = createButton("Ändern");
	changeButton.setOnAction(event -> {
	    setOkPressed(true);
	    enteredCurrentPassword = currentPasswordField.getText();
	    enteredNewPassword = newPasswordField.getText();
	    enteredConfirmationPassword = confirmationPasswordField.getText();
	    patchUserStage.close();
	});

	Button uploadImageButton = createButton("Bild hochladen");
	uploadImageButton.setOnAction(event -> {
	    FileChooser fileChooser = new FileChooser();
	    // Dateiformat-Einschränkungen setzen
	    fileChooser.getExtensionFilters()
		    .addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
	    File selectedFile = fileChooser.showOpenDialog(patchUserStage);
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

	VBox buttonBox = new VBox(20, uploadImageButton, changeButton);
	buttonBox.setAlignment(Pos.CENTER_RIGHT);
	buttonBox.setPadding(new Insets(10, 0, 20, 0));

	grid.add(usernameLabel, 0, 1);
	grid.add(username, 1, 1);
	grid.add(currentPasswordLabel, 0, 2);
	grid.add(currentPasswordField, 1, 2);
	grid.add(newPasswordLabel, 0, 3);
	grid.add(newPasswordField, 1, 3);
	grid.add(confirmationPasswordLabel, 0, 4);
	grid.add(confirmationPasswordField, 1, 4);
	grid.add(buttonBox, 1, 5);

	VBox root = new VBox(infoBox, grid);
	root.getStylesheets().add("/stylesheet.css");
	root.getStyleClass().add("background-black");
	Scene scene = new Scene(root, 330, 350);
	patchUserStage.setScene(scene);
	patchUserStage.showAndWait(); // Warten, bis das Änderungs-Fenster geschlossen ist
    }

}
