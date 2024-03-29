package pong.javafx.user.task;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import pong.javafx.PongApplication;
import pong.javafx.user.view.UserConfirmationErrorDialog;

public class GetDownloadUserImageTask extends Task<Image> {

    public static final Logger logger = Logger.getLogger(GetDownloadUserImageTask.class);

    private String bearerToken;

    public GetDownloadUserImageTask(String bearerToken) {
	this.bearerToken = bearerToken;
    }

    @Override
    protected Image call() {
	// URL API Endpunkt
	String url = PongApplication.API_HOST + "/users/image";
	RestTemplate restTemplate = new RestTemplate();

	try {
	    // Header mit Bearer-Token
	    HttpHeaders headers = new HttpHeaders();
	    headers.set("Authorization", "Bearer " + bearerToken);

	    // RequestEntity mit HttpHeader
	    HttpEntity<String> requestEntity = new HttpEntity<>(headers);

	    // GET Anfrage
	    ResponseEntity<byte[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity,
		    byte[].class);

	    // Antwort verarbeiten (hier nur die Statuscode-Überprüfung)
	    if (responseEntity.getStatusCodeValue() == 200) {
		if (responseEntity.getBody() == null) {
		    return null;
		}
		try (ByteArrayInputStream inputStream = new ByteArrayInputStream(responseEntity.getBody())) {
		    return new Image(inputStream, 100, 0, true, true);
		} catch (IOException e) {
		    logger.error("Image input stream error: " + e.getMessage());
		}
	    }
	} catch (RestClientException e) {
	    logger.error("Http status image download error: " + e.getMessage());
	    if (e instanceof HttpStatusCodeException) {
		final HttpStatus status = ((HttpStatusCodeException) e).getStatusCode();
		// TODO wirklich anzeigen für den User?
		Platform.runLater(() -> UserConfirmationErrorDialog.showErrorDialog("Bilddownload fehlgeschlagen",
			"Der Download des Profilbild ist mit folgendem Statuscode fehlgeschlagen: " + status));
	    } else {
		System.err.println("Fehler beim HTTP-Request: " + e.getLocalizedMessage());
	    }
	}
	return null;
    }
}
