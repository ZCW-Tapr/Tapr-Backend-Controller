package com.Tapr.Trackpad_Controller.DataTransferObject.GetDeviceState;

import com.Tapr.Trackpad_Controller.DataTransferObject.ControlOfDevices.GoveeControlPayload;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoveeStateRequest {

    private String requestId;
    private GoveeStatePayload payload;
}
