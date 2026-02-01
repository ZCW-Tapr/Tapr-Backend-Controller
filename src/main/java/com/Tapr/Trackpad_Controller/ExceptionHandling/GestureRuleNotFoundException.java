package com.Tapr.Trackpad_Controller.ExceptionHandling;

public class GestureRuleNotFoundException extends RuntimeException{

    public GestureRuleNotFoundException(Long id) {
        super("Gesture " + id + " could not be found");
    }
}
