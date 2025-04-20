package fr.teampeps.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class DateParsingException extends RuntimeException {
    public DateParsingException(String message) {
        super(message);
    }
}