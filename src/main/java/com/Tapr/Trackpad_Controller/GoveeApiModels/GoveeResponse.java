package com.Tapr.Trackpad_Controller.GoveeApiModels;


import com.Tapr.Trackpad_Controller.DataTransferObject.GetDeviceState.GoveeStateRequest;
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
