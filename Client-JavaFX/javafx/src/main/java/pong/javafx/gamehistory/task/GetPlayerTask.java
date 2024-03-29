package pong.javafx.gamehistory.task;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.application.Platform;
import javafx.concurrent.Task;
import pong.javafx.PongApplication;
import pong.javafx.gamehistory.model.GameRecord;
import pong.javafx.user.view.UserConfirmationErrorDialog;

public class GetPlayerTask extends Task<List<GameRecord>> {
    
    public static final Logger logger = Logger.getLogger(GetPlayerTask.class);

    private String bearerToken;

    public GetPlayerTask(String bearerToken) {
	this.bearerToken = bearerToken;
    }

    @Override
    protected List<GameRecord> call() {
	// URL API Endpunkt
	String url = PongApplication.API_HOST + "/player";
	RestTemplate restTemplate = new RestTemplate();
	ObjectMapper mapper = new ObjectMapper();
	List<GameRecord> result = new ArrayList<>();

	try {
	    // Header mit Bearer-Token
	    HttpHeaders headers = new HttpHeaders();
	    headers.set("Authorization", "Bearer " + bearerToken);

	    // RequestEntity mit HttpHeader
	    HttpEntity<String> request = new HttpEntity<>(headers);

	    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

	    if (response.getStatusCodeValue() == 200) {
		if (response.getBody() == null) {
		    return result;
		}
		// JSON-Antwort in eine Liste von GameRecord-Objekten parsen
		try {
		    result.addAll(mapper.readValue(response.getBody(), new TypeReference<List<GameRecord>>() {
		    }));
		} catch (JsonProcessingException e) {
		    System.err.println("Fehler beim Json-Processing: " + e.getLocalizedMessage());
		}
		return result;
	    }
	} catch (RestClientException e) {
	    if (e instanceof HttpStatusCodeException) {
		final HttpStatus status = ((HttpStatusCodeException) e).getStatusCode();
		Platform.runLater(() -> UserConfirmationErrorDialog.showErrorDialog(
			"Download der Spielhistorie fehlgeschlagen",
			"Der Download der Spielhistorie ist mit folgendem Statuscode fehlgeschlagen: " + status));
	    } else {
		String errorMsg = "Fehler beim HTTP-Request: " + e.getLocalizedMessage();
		logger.error(errorMsg);
		System.err.println(errorMsg);
	    }
	}
	return result;
    }

}
