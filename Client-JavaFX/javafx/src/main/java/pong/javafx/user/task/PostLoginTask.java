package pong.javafx.user.task;

import java.math.BigInteger;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import javafx.application.Platform;
import javafx.concurrent.Task;
import pong.javafx.PongApplication;
import pong.javafx.user.model.ActiveUser;
import pong.javafx.user.model.User;
import pong.javafx.user.view.UserConfirmationErrorDialog;

public class PostLoginTask extends Task<User> {

    public static final Logger logger = Logger.getLogger(PostLoginTask.class);

    private String email;

    private String password;

    public PostLoginTask(String email, String password) {
	this.email = email;
	this.password = password;
    }

    @Override
    protected User call() {
	String url = PongApplication.API_HOST + "/auth/authenticate";
	RestTemplate restTemplate = new RestTemplate();

	if (!isCancelled()) {
	    try {
		JSONObject requestJson = new JSONObject();
		requestJson.put("email", email);
		requestJson.put("password", password);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> request = new HttpEntity<>(requestJson.toString(), headers);

		// POST-Anfrage
		ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

		if (response.getStatusCodeValue() == 200) {
		    // Verarbeiten der JSON-Antwort
		    String jsonResponse = response.getBody();
		    JSONObject jsonResponseObject = new JSONObject(jsonResponse);
		    String token = jsonResponseObject.getString("access_token");
		    JSONObject jsonPlayer = jsonResponseObject.getJSONObject("player");
		    String playername = jsonPlayer.getString("playername");
		    int playerId = jsonPlayer.getInt("id");
		    User activeUser = new User(email, token, playerId, playername);
		    ActiveUser.getInstance().setUser(activeUser);
		}
	    } catch (RestClientException e) {
		logger.error("Http status error login: " + e.getMessage());
		if (e instanceof HttpStatusCodeException) {
		    final HttpStatus status = ((HttpStatusCodeException) e).getStatusCode();
		    if (status == HttpStatus.NOT_FOUND) {
			// Anmeldung fehlgeschlagen (User does not exist)
			String jsonResponse = ((RestClientResponseException) e).getResponseBodyAsString();
			JSONObject jsonResponseObject = new JSONObject(jsonResponse);
			if (jsonResponseObject.getBigInteger("statusCode").equals(new BigInteger("101"))) {
			    Platform.runLater(() -> UserConfirmationErrorDialog.showErrorDialog("Login fehlgeschlagen",
				    "Der Benutzer mit der E-Mail-Adresse existiert nicht: " + email));
			} else {
			    Platform.runLater(() -> UserConfirmationErrorDialog.showErrorDialog("Login fehlgeschlagen",
				    "Login mit folgendem Statuscode fehlgeschlagen: " + status));
			}

		    } else if (status == HttpStatus.INTERNAL_SERVER_ERROR) {
			// Anmeldung fehlgeschlagen (Bad credentials)
			Platform.runLater(() -> UserConfirmationErrorDialog.showErrorDialog("Login fehlgeschlagen",
				"Die Zugangsdaten für die E-Mail-Adresse sind nicht korrekt: " + email));
		    } else {
			// Andere Fehler
			Platform.runLater(() -> UserConfirmationErrorDialog.showErrorDialog("Login fehlgeschlagen",
				"Login mit folgendem Statuscode fehlgeschlagen: " + status));
		    }
		} else {
		    System.err.println("Fehler beim HTTP-Request: " + e.getLocalizedMessage());
		}
	    }
	}
	return ActiveUser.getInstance().getUser();
    }

}
