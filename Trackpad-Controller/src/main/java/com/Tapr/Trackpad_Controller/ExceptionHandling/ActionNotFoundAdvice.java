package com.Tapr.Trackpad_Controller.ExceptionHandling;

import com.Tapr.Trackpad_Controller.Entities.Action;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ActionNotFoundAdvice {

    @ExceptionHandler(ActionNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String actionNotFoundHandler(ActionNotFoundException exception) {
        return exception.getMessage();
    }
}
