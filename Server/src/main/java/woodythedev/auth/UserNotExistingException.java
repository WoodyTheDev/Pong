package woodythedev.auth;

public class UserNotExistingException extends OwnAuthenticationException {
    public final static int ERROR_CODE = 101;

    protected int getErrorCode() {
        return ERROR_CODE;
    }

    public UserNotExistingException() {
        super("User does not exist");
    }
}
