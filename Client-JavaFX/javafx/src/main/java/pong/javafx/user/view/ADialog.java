package pong.javafx.user.view;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public abstract class ADialog {

    private Stage primaryStage;
    
    protected GridPane grid;

    public ADialog(Stage primaryStage) {
	this.primaryStage = primaryStage;
	this.grid =  new GridPane();
	this.grid.getStylesheets().add("/stylesheet.css");
	this.grid.getStyleClass().add("background-black");
    }

    // Methode Erstellen eines Textfelds
    protected TextField createTextField() {
	TextField textField = new TextField();
	textField.getStylesheets().add("/stylesheet.css");
	textField.getStyleClass().add("dialog-field");
	return textField;
    }

    // Methode zum Erstellen eines Label
    protected Label createLabel(String labelText) {
	Label label = new Label(labelText);
	label.getStylesheets().add("/stylesheet.css");
	label.getStyleClass().add("dialog-label");
	return label;
    }

    // Methode zum Erstellen eines Buttons
    protected Button createButton(String buttonText) {
	Button button = new Button(buttonText);
	button.getStylesheets().add("/stylesheet.css");
	button.getStyleClass().add("dialog-field");
	return button;
    }

    // Methode zum Erstellen eines PasswordFields
    protected PasswordField createPasswordField() {
	PasswordField passwordField = new PasswordField();
	passwordField.getStylesheets().add("/stylesheet.css");
	passwordField.getStyleClass().add("dialog-field");
	passwordField.setPromptText("dein Passwort");
	return passwordField;
    }
    
    // Methode zum Erstellen einer Stage
    protected Stage createStage(String title, int width, int height) {
	Stage stage = new Stage();
	stage.initModality(Modality.WINDOW_MODAL);
	stage.initOwner(primaryStage);
	stage.setTitle(title);
	stage.setWidth(width);
	stage.setHeight(height);
	return stage;
    }
}
