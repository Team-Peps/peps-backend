package fr.teampeps.exceptions;

public class UploadImageException extends RuntimeException {
    public UploadImageException(String message, Exception e) {
        super(message, e);
    }
}
