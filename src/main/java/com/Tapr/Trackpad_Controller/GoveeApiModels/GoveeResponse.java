package com.Tapr.Trackpad_Controller.GoveeApiModels;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoveeResponse {

    private int code;
    private String message;
    private List<DeviceData> data;
    private DeviceData payload;
}
