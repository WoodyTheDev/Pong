package pong.javafx.user.view;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DialogPane;

public class UserConfirmationErrorDialog {

    // Methode zur Anzeige eines Fehlerdialogs
    public static void showErrorDialog(String title, String content) {
	showDialog(title, content, true);
    }
    
    // Methode zur Anzeige eines Informationsdialogs
    public static void showInformationDialog(String title, String content) {
	showDialog(title, content, false);
    }

    private static void showDialog(String title, String content, boolean isError) {
	Alert alert = new Alert(isError? AlertType.ERROR : AlertType.INFORMATION);
	DialogPane dialogPane = alert.getDialogPane();
	dialogPane.getStylesheets().add("/stylesheet.css");
	dialogPane.getStyleClass().add("custom-alert");

	alert.setTitle(title);
	alert.setHeaderText(null);
	alert.setContentText(content);
	alert.showAndWait();
    }
}

