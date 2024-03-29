package pong.javafx.user.task;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javafx.concurrent.Task;
import pong.javafx.PongApplication;

public class PostLogoutTask extends Task<Boolean> {

    private String email;

    public PostLogoutTask(String email) {
	this.email = email;
    }

    @Override
    protected Boolean call() {
	String url = PongApplication.API_HOST + "/auth/logout";
	RestTemplate restTemplate = new RestTemplate();

	JSONObject requestJson = new JSONObject();
	requestJson.put("email", email);

	HttpHeaders headers = new HttpHeaders();
	headers.setContentType(MediaType.APPLICATION_JSON);

	HttpEntity<String> request = new HttpEntity<>(requestJson.toString(), headers);

	// POST-Anfrage
	ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
	return response.getStatusCodeValue() == 200 ? true : false;
    }

}
