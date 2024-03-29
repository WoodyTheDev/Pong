package woodythedev.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import woodythedev.ErrorMessage;

@ControllerAdvice
public class AuthenticationControllerExceptionHandler extends ResponseEntityExceptionHandler {
	@ExceptionHandler({UserNotExistingException.class, PlayerNotExistingException.class, UserAlreadyExistException.class})
    public ResponseEntity<ErrorMessage> handleOwnAuthenticationException(OwnAuthenticationException ex, WebRequest request) {
        ErrorMessage message = new ErrorMessage(
            ex.getErrorCode(),
            ex.getMessage(),
            request.getDescription(false));
		HttpStatus responseHttpStatus = HttpStatus.FORBIDDEN;
		switch(ex.getClass().getSimpleName()) {
			case AuthenticationExceptions.UserNotExistingException:
			case AuthenticationExceptions.PlayerNotExistingException:
			responseHttpStatus = HttpStatus.NOT_FOUND;
			break;
			case AuthenticationExceptions.UserAlreadyExistException:
			responseHttpStatus = HttpStatus.NOT_ACCEPTABLE;
			break;
		}
        return new ResponseEntity<>(message, responseHttpStatus);
    }

	private interface AuthenticationExceptions {
		String UserNotExistingException = "UserNotExistingException";
		String PlayerNotExistingException = "PlayerNotExistingException";
		String UserAlreadyExistException = "UserAlreadyExistException";
	}

}
