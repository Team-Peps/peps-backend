package fr.teampeps.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return buildErrorResponse(
                "La ressource demandée est introuvable.",
                ex.getMessage(),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        return buildErrorResponse(
                "Les données fournies ne sont pas valides.",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneralException(Exception ex) {
        return buildErrorResponse(
                "Une erreur est survenue. Veuillez réessayer plus tard.",
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(RuntimeException ex) {
        return buildErrorResponse(
                "Une erreur est survenue. Veuillez réessayer plus tard.",
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        return buildErrorResponse("Cette ressource existe déjà.", ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDenied(AccessDeniedException ex) {
        return buildErrorResponse("Accès refusé", ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MissingImageException.class)
    public ResponseEntity<Object> handleMissingImageException(MissingImageException ex) {
        return buildErrorResponse("L'image est obligatoire pour cette opération.", ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<Object> handleMissingPart(MissingServletRequestPartException ex) {
        if ("imageFile".equals(ex.getRequestPartName())) {
            return buildErrorResponse(
                    "Le fichier image est requis.",
                    ex.getMessage(),
                    HttpStatus.BAD_REQUEST
            );
        }

        return buildErrorResponse(
                "Un champ obligatoire est manquant.",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return buildErrorResponse(
                "Des champs sont invalides.",
                errors.toString(),
                HttpStatus.BAD_REQUEST
        );
    }


    private ResponseEntity<Object> buildErrorResponse(String userMessage, String technicalMessage, HttpStatus status) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("status", status.value());
        errorDetails.put("error", status.getReasonPhrase());
        errorDetails.put("message", userMessage);
        errorDetails.put("details", technicalMessage);

        return new ResponseEntity<>(errorDetails, status);
    }
}
