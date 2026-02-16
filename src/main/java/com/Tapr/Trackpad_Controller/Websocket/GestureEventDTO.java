package com.Tapr.Trackpad_Controller.Websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GestureEventDTO {

    private Integer fingerCount;
    private String gestureType;
    private String value;
}
