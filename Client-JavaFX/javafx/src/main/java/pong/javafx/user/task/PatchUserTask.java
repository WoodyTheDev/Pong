package pong.javafx.user.task;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.application.Platform;
import javafx.concurrent.Task;
import pong.javafx.PongApplication;
import pong.javafx.user.model.ActiveUser;
import pong.javafx.user.view.UserConfirmationErrorDialog;

public class PatchUserTask extends Task<Boolean> {

    public static final Logger logger = Logger.getLogger(PatchUserTask.class);

    private String bearerToken;

    private String currentPassword;

    private String newPassword;

    private String confirmationPassword;

    public PatchUserTask(String currentPassword, String newPassword, String confirmationPassword) {
	this.bearerToken = ActiveUser.getInstance().getUser().getBearerToken().getValue();
	this.currentPassword = currentPassword;
	this.newPassword = newPassword;
	this.confirmationPassword = confirmationPassword;
    }

    @Override
    protected Boolean call() throws Exception {

	String url = PongApplication.API_HOST + "/users";
	RestTemplate restTemplate = new RestTemplate();
	restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
	if (!isCancelled()) {
	    try {
		JSONObject requestJson = new JSONObject();
		requestJson.put("currentPassword", currentPassword);
		requestJson.put("newPassword", newPassword);
		requestJson.put("confirmationPassword", confirmationPassword);

		// Header mit Bearer-Token
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + bearerToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		// RequestEntity mit HttpHeader
		HttpEntity<String> requestEntity = new HttpEntity<>(requestJson.toString(), headers);

		// PATCH Anfrage
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PATCH, requestEntity,
			String.class);
		if (response.getStatusCodeValue() == 200) {
		    return true;
		}
	    } catch (HttpServerErrorException e) {
		final HttpStatus status = e.getStatusCode();
		logger.error("Http status error patch user: " + e.getMessage());
		if (status == HttpStatus.INTERNAL_SERVER_ERROR) {
		    String jsonResponse = e.getResponseBodyAsString();
		    ObjectMapper objectMapper = new ObjectMapper();
		    JsonNode jsonNode = objectMapper.readTree(jsonResponse);
		    String message = jsonNode.get("message").asText();
		    if (message.equals("Password are not the same")) {
			Platform.runLater(() -> UserConfirmationErrorDialog.showErrorDialog(
				"Passwort Ändern fehlgeschlagen",
				"Das Ändern der Benutzerpassworts ist fehlgeschlagen, da die Passwörter nicht übereinstimmen."));
		    } else if (message.equals("Wrong password")) {
			Platform.runLater(() -> UserConfirmationErrorDialog.showErrorDialog(
				"Passwort Ändern fehlgeschlagen",
				"Das Ändern der Benutzerpassworts ist fehlgeschlagen, da das aktuelle Passwort nicht mit dem im Benutzer hinterlegten übereinstimmt."));
		    } else {
			// Andere Fehler
			Platform.runLater(
				() -> UserConfirmationErrorDialog.showErrorDialog("Passwort Ändern fehlgeschlagen",
					"Ändern des Passwortes mit folgendem Statuscode fehlgeschlagen: " + status));
		    }
		} else {
		    // Andere Fehler
		    Platform.runLater(
			    () -> UserConfirmationErrorDialog.showErrorDialog("Passwort Ändern fehlgeschlagen",
				    "Ändern des Passwortes mit folgendem Statuscode fehlgeschlagen: " + status));
		}
	    }
	}
	return false;
    }
}
