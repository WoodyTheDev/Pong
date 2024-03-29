package pong.javafx.game.view;

import java.io.FileNotFoundException;

import javafx.scene.control.Button;

public class MenuButton extends Button {

    public static final String MENU = "button-menu";

    public static final String MENU_HOVER = "button-menu-hover";

    public static final String MENU_DARK = "button-menu-dark";
    
    public static final String MENU_DARK_HOVER = "button-menu-dark-hover";

    public static final String GAME = "button-game";

    public static final String GAME_HOVER = "button-game-hover";

    public static final String USER = "button-user";

    public static final String USER_HOVER = "button-user-hover";

    public static final String EXIT = "button-exit";

    public static final String EXIT_HOVER = "button-exit-hover";

    public MenuButton(String text, int layoutX, int layoutY, String style) throws FileNotFoundException {
	this.setText(text);
	this.setLayoutX(layoutX);
	this.setLayoutY(layoutY);
	this.setPrefSize(350, 50);
	this.getStylesheets().add("/stylesheet.css");
	this.getStyleClass().add(style);
	addListeners();
    }

    public void addListeners() {
	this.setOnMouseEntered(e -> {
	    toggleButtonStyle(this);
	});

	this.setOnMouseExited(e -> {
	    toggleButtonStyle(this);
	});
    }

    private void toggleButtonStyle(MenuButton menuButton) {

	if (!menuButton.getStyleClass().isEmpty()) {

	    // Das letzte Element der CSS-Klassenliste holen
	    String currentStyle = menuButton.getStyleClass().get(menuButton.getStyleClass().size() - 1);
	    menuButton.getStyleClass().remove(currentStyle);

	    switch (currentStyle) {
	    case MENU:
		this.getStyleClass().add(MENU_HOVER);
		break;
	    case MENU_DARK:
		this.getStyleClass().add(MENU_DARK_HOVER);
		break;
	    case GAME:
		this.getStyleClass().add(GAME_HOVER);
		break;
	    case USER:
		this.getStyleClass().add(USER_HOVER);
		break;
	    case EXIT:
		this.getStyleClass().add(EXIT_HOVER);
		break;
	    case MENU_HOVER:
		this.getStyleClass().add(MENU);
		break;
	    case MENU_DARK_HOVER:
		this.getStyleClass().add(MENU_DARK);
		break;
	    case GAME_HOVER:
		this.getStyleClass().add(GAME);
		break;
	    case USER_HOVER:
		this.getStyleClass().add(USER);
		break;
	    case EXIT_HOVER:
		this.getStyleClass().add(EXIT);
		break;
	    default:
		break;
	    }

	}
    }

}
