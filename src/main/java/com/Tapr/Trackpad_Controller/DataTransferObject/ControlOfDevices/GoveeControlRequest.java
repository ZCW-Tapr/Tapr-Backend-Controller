package com.Tapr.Trackpad_Controller.DataTransferObject.ControlOfDevices;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoveeControlRequest {

    private String requestId;
    private GoveeControlPayload payload;
}
