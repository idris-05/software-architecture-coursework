package dz.hissab.exception;

public class OcrProcessingException extends HissabException {
    public OcrProcessingException(String message) {
        super(message);
    }

    public OcrProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
