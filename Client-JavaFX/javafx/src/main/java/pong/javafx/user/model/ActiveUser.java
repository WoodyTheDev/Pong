package pong.javafx.user.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class ActiveUser {

    private static ActiveUser instance;

    private ObjectProperty<User> activeUserProperty = new SimpleObjectProperty<>();
    
    private ActiveUser() {
	// Privater Konstruktor
    }

    public static ActiveUser getInstance() {
	if (instance == null) {
	    instance = new ActiveUser();
	}
	return instance;
    }

    public void setUser(User activeUser) {
	activeUserProperty.set(activeUser);
    }
    
    public User getUser() {
	return activeUserProperty.get();
    }

    public ObjectProperty<User> activeUserProperty() {
	return activeUserProperty;
    }
}