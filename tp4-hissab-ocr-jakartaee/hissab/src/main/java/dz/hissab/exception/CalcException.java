package dz.hissab.exception;

public class CalcException extends HissabException {
    public CalcException(String message) {
        super(message);
    }

    public CalcException(String message, Throwable cause) {
        super(message, cause);
    }
}
