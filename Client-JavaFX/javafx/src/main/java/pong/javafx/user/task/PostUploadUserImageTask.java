package pong.javafx.user.task;

import java.io.File;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javafx.concurrent.Task;
import pong.javafx.PongApplication;

public class PostUploadUserImageTask extends Task<Boolean> {

    private File imageFile;

    private String bearerToken;

    public PostUploadUserImageTask(File imageFile, String bearerToken) {
	this.bearerToken = bearerToken;
	this.imageFile = imageFile;
    }

    @Override
    protected Boolean call() {

	// URL API-Endpunkt
	String url = PongApplication.API_HOST + "/users/image";

	// Header mit Bearer-Token
	HttpHeaders headers = new HttpHeaders();
	headers.set("Authorization", "Bearer " + bearerToken);

	// Content-Type auf multipart/form-data
	headers.setContentType(MediaType.MULTIPART_FORM_DATA);

	// Request-Body mit der Datei
	MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
	body.add("image", new FileSystemResource(new File(imageFile.getPath())));

	// RequestEntity mit der MultiValueMap und HttpHeaders
	HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

	RestTemplate restTemplate = new RestTemplate();
	ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
		String.class);

	// Antwort verarbeiten (hier nur Statuscode-Überprüfung)
	return responseEntity.getStatusCodeValue() == 200 ? true : false;
    }
}
