package exceptions;

public class SessionLimitReachedException extends RuntimeException {
    public SessionLimitReachedException(String message) {
        super(message);
    }
}
