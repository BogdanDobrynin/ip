package novichok.exceptions;

public class NovichokException extends Exception{
    public NovichokException(String message) {
        super(message);
    }

    public NovichokException(String message, Throwable cause) {
        super(message, cause);
    }
}
