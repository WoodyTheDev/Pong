package woodythedev;

public abstract class PongException extends RuntimeException{
	
	protected abstract int getErrorCode();
	public PongException(String msg) {
		super(msg);
	}
	
}
