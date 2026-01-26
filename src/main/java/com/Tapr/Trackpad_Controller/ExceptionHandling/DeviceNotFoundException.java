package com.Tapr.Trackpad_Controller.ExceptionHandling;

public class DeviceNotFoundException extends RuntimeException{

    public DeviceNotFoundException(Long id) {
        super("Device " + id + " could not be found");
    }
}
