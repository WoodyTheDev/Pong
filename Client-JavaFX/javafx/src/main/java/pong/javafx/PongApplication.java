package pong.javafx;

import org.apache.log4j.xml.DOMConfigurator;

import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import pong.javafx.game.view.StartView;

/**
 * * Class which starts the JavaFX Application and opens the PONG game.
 *
 * @author Daniela Kothe
 * @version 1.0.0 20.11.2023
 */
public class PongApplication extends Application {

    public static final String APP_TITLE = "PONG Game";
    // public static final String API_HOST = "http://localhost:8080/api/v1";
    public static final String API_HOST = "http://lyra.et-inf.fho-emden.de:19036/api/v1";

    @Override
    public void start(Stage primaryStage) throws Exception {
	Font.loadFont(PongApplication.class.getResourceAsStream("/my_font.ttf"), 12);
	StartView startView = new StartView();
	primaryStage = startView.getMainStage();
	primaryStage.setTitle(APP_TITLE);
	primaryStage.show();
    }

    /**
     * Main method which launches the application.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
	// darf und muss nur einmalig ausgeführt werden!
	DOMConfigurator.configureAndWatch("/log4j.xml");
	launch(args);
    }

}