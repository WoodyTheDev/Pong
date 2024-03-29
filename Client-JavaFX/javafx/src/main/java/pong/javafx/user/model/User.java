package pong.javafx.user.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import lombok.Getter;

public class User {

    @Getter
    public int id;

    @Getter
    public StringProperty email;

    @Getter
    public StringProperty userName;

    @Getter
    public StringProperty bearerToken;

    @Getter
    private ObjectProperty<Image> profilePicture;

    public User(String email, String bearerToken, int playerId, String playername) {
	this.email = new SimpleStringProperty(email);
	this.bearerToken = new SimpleStringProperty(bearerToken);
	this.profilePicture = new SimpleObjectProperty<Image>();
	this.id = playerId;
	this.userName = new SimpleStringProperty(playername);
    }
    
    public void setProfilePicture(Image image) {
	this.profilePicture = new SimpleObjectProperty<Image>(image);
    }

}
