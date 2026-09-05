package dz.hissab.exception;

public class HissabException extends Exception {
    public HissabException(String message) {
        super(message);
    }

    public HissabException(String message, Throwable cause) {
        super(message, cause);
    }
}
