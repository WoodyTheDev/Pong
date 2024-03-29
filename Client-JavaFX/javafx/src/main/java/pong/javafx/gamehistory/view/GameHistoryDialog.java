package pong.javafx.gamehistory.view;

import java.io.IOException;

import org.apache.log4j.Logger;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class GameHistoryDialog {
    
    public static final Logger logger = Logger.getLogger(GameHistoryDialog.class);

    public GameHistoryDialog() {
    }

    public Stage enterGameHistoryStage() {
	try {
	    FXMLLoader loader = new FXMLLoader(getClass().getResource("/dialog/game_history_dialog.fxml"));
	    Parent root = loader.load();
	    Stage secondStage = new Stage();
	    secondStage.getIcons().add(new Image(getClass() //
		    .getResource("/clock.png").toString()));
	    secondStage.initStyle(StageStyle.DECORATED);
	    secondStage.setTitle("Spielhistorie");
	    secondStage.setScene(new Scene(root, 920, 620));
	    return secondStage;
	} catch (IOException e) {
	    logger.error("Failed to load game history stage: " + e.getMessage());
	    return null;
	}
    }

}
