package fr.teampeps.exceptions;

public class MissingImageException extends RuntimeException {
    public MissingImageException(String message) {
        super(message);
    }
}
