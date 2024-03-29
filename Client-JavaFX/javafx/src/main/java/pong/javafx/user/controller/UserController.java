package pong.javafx.user.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import pong.javafx.user.model.ActiveUser;
import pong.javafx.user.model.User;
import pong.javafx.user.task.GetDownloadUserImageTask;
import pong.javafx.user.task.PatchUserTask;
import pong.javafx.user.task.PostLoginTask;
import pong.javafx.user.task.PostLogoutTask;
import pong.javafx.user.task.PostRegisterTask;
import pong.javafx.user.task.PostUploadUserImageTask;
import pong.javafx.user.view.LoginDialog;
import pong.javafx.user.view.PatchUserDialog;
import pong.javafx.user.view.RegisterDialog;
import pong.javafx.user.view.UserConfirmationErrorDialog;

public class UserController {

    public static final Logger logger = Logger.getLogger(UserController.class);

    private StringProperty userNameProperty = new SimpleStringProperty();

    private ObjectProperty<Image> userProfilePictureProperty = new SimpleObjectProperty<>();

    public UserController() {
    }

    public void initialize() {
	ActiveUser.getInstance().activeUserProperty().addListener((obsUsr, oldUser, newUser) -> {
	    updateActiveUserUI();
	});
    }

    public StringProperty userNameProperty() {
	return userNameProperty;
    }

    public ObjectProperty<Image> userImageProperty() {
	return userProfilePictureProperty;
    }

    public void updateActiveUserUI() {
	User activeUser = ActiveUser.getInstance().getUser();

	Platform.runLater(() -> {
	    if (activeUser != null) {
		userNameProperty.set(activeUser.getUserName().getValue());
		userProfilePictureProperty.set(activeUser.getProfilePicture().getValue());
	    } else {
		userNameProperty.set(null);
		userProfilePictureProperty.set(null);
	    }
	});
    }

    public User loginAction(ActionEvent event, Stage stage) throws FileNotFoundException {
	// Dialog aufrufen und Benutzername und Passwort erhalten
	LoginDialog loginDialog = new LoginDialog(stage);
	loginDialog.showAndWait();
	User activeUser = null;
	if (loginDialog.isOkPressed()) {
	    String email = loginDialog.getEnteredUsername();
	    String password = loginDialog.getEnteredPassword();
	    // Erstellen des PostLoginTask mit Benutzername und Passwort
	    PostLoginTask loginTask = new PostLoginTask(email, password);
	    new Thread(loginTask).start();
	    try {
		activeUser = loginTask.get();
		if (activeUser != null) {
		    ActiveUser.getInstance().setUser(activeUser);
		    imageDownloadAction(event);
		}
	    } catch (InterruptedException | ExecutionException e) {
		logger.error("Error download image: " + e.getMessage());
	    }
	}
	return ActiveUser.getInstance().getUser();
    }

    public boolean logoutAction(ActionEvent event) {
	boolean isLoggedOut = false;
	PostLogoutTask logoutTask = new PostLogoutTask(ActiveUser.getInstance().getUser().getEmail().getValue());
	new Thread(logoutTask).start();
	try {
	    isLoggedOut = logoutTask.get();
	    if (isLoggedOut) {
		ActiveUser.getInstance().setUser(null);
	    }
	    return isLoggedOut;
	} catch (InterruptedException | ExecutionException e) {
	    logger.error("Error logout user: " + e.getMessage());
	}
	return isLoggedOut;
    }

    public boolean patchUserAction(ActionEvent event, Stage stage) throws FileNotFoundException {
	boolean userIsPatched = false;

	PatchUserDialog patchUserDialog = new PatchUserDialog(stage);
	patchUserDialog.showAndWait();
	if (patchUserDialog.isOkPressed()) {
	    String currentPassword = patchUserDialog.getEnteredCurrentPassword();
	    String newPassword = patchUserDialog.getEnteredNewPassword();
	    String confirmationPassword = patchUserDialog.getEnteredConfirmationPassword();
	    Image image = patchUserDialog.getSelectedImage();
	    File imageFile = patchUserDialog.getSelectedImageFile();
	    if (!currentPassword.isEmpty() || !newPassword.isEmpty() || !confirmationPassword.isEmpty()) {
		if (newPassword.isEmpty() && confirmationPassword.isEmpty()) {
		    Platform.runLater(() -> UserConfirmationErrorDialog.showErrorDialog("Ungültiges Passwort",
			    "Das Passwort darf nicht leer sein!"));
		}
		else {
		    PatchUserTask patchUserTask = new PatchUserTask(currentPassword, newPassword, confirmationPassword);
		    new Thread(patchUserTask).start();
		    try {
			userIsPatched = patchUserTask.get();
			if (image != null && imageFile != null) {
			    imageUploadAction(event, imageFile, image);
			}
		    } catch (InterruptedException | ExecutionException e) {
			logger.error("Error patch user: " + e.getMessage());
		    }
		}
	    } else {
		if (image != null && imageFile != null) {
		    imageUploadAction(event, imageFile, image);
		    userIsPatched = true;
		}
	    }
	    return userIsPatched;
	}
	return userIsPatched;
    }

    public User registerAction(ActionEvent event, Stage stage) throws FileNotFoundException {
	// Dialog aufrufen und Benutzername und Passwort erhalten
	RegisterDialog registerDialog = new RegisterDialog(stage);
	registerDialog.showAndWait();
	User activeUser = null;
	if (registerDialog.isOkPressed()) {
	    String email = registerDialog.getEnteredEmail();
	    String playername = registerDialog.getEnteredUsername();
	    String password = registerDialog.getEnteredPassword();
	    Image image = registerDialog.getSelectedImage();
	    File imageFile = registerDialog.getSelectedImageFile();
	    PostRegisterTask registerTask = new PostRegisterTask(email, playername, password);
	    new Thread(registerTask).start();
	    try {
		activeUser = registerTask.get();
		if (activeUser != null && image != null && imageFile != null) {
		    ActiveUser.getInstance().setUser(activeUser);
		    imageUploadAction(event, imageFile, image);
		}
	    } catch (InterruptedException | ExecutionException e) {
		logger.error("Error register user: " + e.getMessage());
	    }
	}
	return ActiveUser.getInstance().getUser();
    }

    public void imageDownloadAction(ActionEvent e3) {
	GetDownloadUserImageTask imageTask = new GetDownloadUserImageTask(
		ActiveUser.getInstance().getUser().getBearerToken().getValue());
	new Thread(imageTask).start();
	try {
	    Image image = imageTask.get();
	    ActiveUser.getInstance().getUser().setProfilePicture(image);
	} catch (InterruptedException | ExecutionException e) {
	    logger.error("Error download image: " + e.getMessage());
	}
    }

    public void imageUploadAction(ActionEvent e3, File imageFile, Image image) {
	PostUploadUserImageTask uploadImageTask = new PostUploadUserImageTask(imageFile,
		ActiveUser.getInstance().getUser().getBearerToken().getValue());
	new Thread(uploadImageTask).start();
	try {
	    if (uploadImageTask.get()) {
		ActiveUser.getInstance().getUser().setProfilePicture(image);
	    }
	} catch (InterruptedException | ExecutionException e) {
	    logger.error("Error upload image: " + e.getMessage());
	}
    }

}
