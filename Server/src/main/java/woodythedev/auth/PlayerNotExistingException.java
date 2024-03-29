package woodythedev.auth;

public class PlayerNotExistingException extends OwnAuthenticationException {
    public static final int ERROR_CODE = 103;

    protected int getErrorCode() {
        return ERROR_CODE;
    }

    public PlayerNotExistingException() {
        super("Player does not exist");
    }
}
