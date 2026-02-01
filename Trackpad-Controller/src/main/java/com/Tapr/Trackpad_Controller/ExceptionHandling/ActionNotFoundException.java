package com.Tapr.Trackpad_Controller.ExceptionHandling;

public class ActionNotFoundException extends RuntimeException{

    public ActionNotFoundException(Long id) {
        super("Action " + id + " could not be found");
    }
}
