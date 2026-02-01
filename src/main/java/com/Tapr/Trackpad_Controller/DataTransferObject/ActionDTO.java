package com.Tapr.Trackpad_Controller.DataTransferObject;

import lombok.Data;

@Data
public class ActionDTO {
    private String actionType;
    private String actionName;
    private String description;

    // Light action details (nested)
    private LightActionDTO lightAction;

    @Data
    public static class LightActionDTO {
        private Long deviceId;
        private String operation;  // "TOGGLE", "SET_COLOR", "SET_BRIGHTNESS"
        private String color;
        private Integer brightness;
        private Integer duration;
    }
}