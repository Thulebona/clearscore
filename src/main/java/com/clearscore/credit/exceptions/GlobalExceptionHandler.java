package com.clearscore.credit.exceptions;


import com.clearscore.credit.exceptions.models.ErrorMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * handle any exception thrown by the application and returns customized message
 * to the user and void return sensitive information
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleException(Exception ex) {
        log.error(ex.getMessage(), ex);

        var apiError = new ErrorMessage()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message("Something went wrong, please contact support");
        return ResponseEntity.internalServerError().body(apiError);
    }

    @ExceptionHandler(CustomClientException.class)
    protected ResponseEntity<Object> handleCustomClientException(CustomClientException ex) {
        var message = String.format("%s error: %s", ex.getProvider().getName(), ex.getMessage());
        log.error(message, ex);

        var apiError = new ErrorMessage()
                .status(HttpStatus.BAD_REQUEST)
                .message(message);
        return ResponseEntity.badRequest().body(apiError);
    }

}
