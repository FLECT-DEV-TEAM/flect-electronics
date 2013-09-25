package exception;

public class SessionTimeoutException extends RuntimeException {
    public SessionTimeoutException(String message) {
        super(message);
    }
    
    public SessionTimeoutException(String message, Throwable t) {
        super(message, t);
    }
    
    public SessionTimeoutException(Throwable t) {
        super(t);
    }
}
