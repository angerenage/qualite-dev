package org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.error;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.univ_paris8.iut.montreuil.arollet.qualite_dev.web.dto.ErrorResponseDto;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponseDto> handleApi(ApiException ex) {
        return build(ex.getStatus(), ex.getMessages());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidation(MethodArgumentNotValidException ex) {
        List<String> messages = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            messages.add(error.getField() + ": " + error.getDefaultMessage());
        }
        if (messages.isEmpty()) {
            messages.add("Validation failed.");
        }
        return build(HttpStatus.BAD_REQUEST, messages);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleConstraint(ConstraintViolationException ex) {
        List<String> messages = ex.getConstraintViolations().stream().map(v -> v.getPropertyPath() + ": " + v.getMessage()).toList();
        return build(HttpStatus.BAD_REQUEST, messages);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDto> handleAccessDenied(AccessDeniedException ex) {
        return build(HttpStatus.FORBIDDEN, List.of("Access denied."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGeneric(Exception ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, List.of("Internal server error."));
    }

    private ResponseEntity<ErrorResponseDto> build(HttpStatus status, List<String> messages) {
        return ResponseEntity.status(status).body(new ErrorResponseDto(status.getReasonPhrase(), messages));
    }
}
