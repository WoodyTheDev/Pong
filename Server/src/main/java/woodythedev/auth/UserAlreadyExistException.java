package woodythedev.auth;

public class UserAlreadyExistException extends OwnAuthenticationException {
    public final static int ERROR_CODE = 100;

    protected int getErrorCode() {
        return ERROR_CODE;
    }

    public UserAlreadyExistException() {
        super("User already exists for this email");
    }
}
