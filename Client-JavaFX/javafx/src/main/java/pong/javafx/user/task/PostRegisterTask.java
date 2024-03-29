package pong.javafx.user.task;

import java.math.BigInteger;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javafx.application.Platform;
import javafx.concurrent.Task;
import pong.javafx.PongApplication;
import pong.javafx.user.model.User;
import pong.javafx.user.view.UserConfirmationErrorDialog;

public class PostRegisterTask extends Task<User> {

    public static final Logger logger = Logger.getLogger(PostRegisterTask.class);

    private String email;

    private String playername;

    private String password;

    public PostRegisterTask(String email, String playername, String password) {
	this.email = email;
	this.playername = playername;
	this.password = password;
    }

    @Override
    protected User call() {
	String url = PongApplication.API_HOST + "/auth/register";
	RestTemplate restTemplate = new RestTemplate();

	if (!isCancelled()) {
	    try {
		// JSON-Objekts mit den Registrierungsinformationen
		JSONObject requestJson = new JSONObject();
		requestJson.put("email", email);
		requestJson.put("password", password);
		requestJson.put("playername", playername);
		requestJson.put("role", "PLAYER");

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
		    int playerId = jsonPlayer.getInt("id");
		    Platform.runLater(() -> UserConfirmationErrorDialog.showInformationDialog(
			    "Registrierung erfolgreich",
			    "Der Benutzer " + playername + " wurde erfolgreich angelegt und ist jetzt eingeloggt."));
		    return new User(email, token, playerId, playername);
		}
	    } catch (HttpClientErrorException e) {
		// TODO welche Codes gibt es?
		final HttpStatus status = e.getStatusCode();
		logger.error("Http status error register: " + e.getResponseBodyAsString());
		if (status == HttpStatus.NOT_ACCEPTABLE) {
		    // Registrierung fehlgeschlagen
		    String jsonResponse = e.getResponseBodyAsString();
		    JSONObject jsonResponseObject = new JSONObject(jsonResponse);
		    if (jsonResponseObject.getBigInteger("statusCode").equals(new BigInteger("100"))) {
			Platform.runLater(
				() -> UserConfirmationErrorDialog.showErrorDialog("Registrierung fehlgeschlagen",
					"Es gibt schon einen Benutzer mit der E-Mail-Adresse: " + email));
		    } else {
			Platform.runLater(
				() -> UserConfirmationErrorDialog.showErrorDialog("Registrierung fehlgeschlagen",
					"Registrierung mit folgendem Statuscode fehlgeschlagen: " + status));
		    }
		} else {
		    // Andere Fehler
		    Platform.runLater(() -> UserConfirmationErrorDialog.showErrorDialog("Registrierung fehlgeschlagen",
			    "Registrierung mit folgendem Statuscode fehlgeschlagen: " + status));
		}
	    }
	}
	return null;
    }

}