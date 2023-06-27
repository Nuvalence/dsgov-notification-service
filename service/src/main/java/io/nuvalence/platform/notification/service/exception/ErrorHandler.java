package io.nuvalence.platform.notification.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;

/**
 * Handles exceptions thrown by controllers.
 */
@ControllerAdvice
public class ErrorHandler {

    /**
     * Handles ConstraintViolationException.
     *
     * @param exception ConstraintViolationException
     * @return ResponseEntity
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolationException(
            ConstraintViolationException exception) {
        ProblemDetail problemDetail =
                ProblemDetail.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .title(exception.getMessage())
                        .detail(exception.getConstraintViolations().toString())
                        .build();
        return ResponseEntity.badRequest().body(problemDetail);
    }
}
