package woodythedev.auth;

import org.springframework.security.core.AuthenticationException;

public abstract class OwnAuthenticationException extends AuthenticationException {
	
	protected abstract int getErrorCode();
	public OwnAuthenticationException(String msg) {
		super(msg);
	}
	
}
