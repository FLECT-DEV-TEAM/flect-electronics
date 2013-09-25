package exception;

public class PlayRuntimeException extends RuntimeException {
    
    public PlayRuntimeException(String message) {
        super(message);
    }
    
    public PlayRuntimeException(String message, Throwable t) {
        super(message, t);
    }
    
    public PlayRuntimeException(Throwable t) {
        super(t);
    }
}
