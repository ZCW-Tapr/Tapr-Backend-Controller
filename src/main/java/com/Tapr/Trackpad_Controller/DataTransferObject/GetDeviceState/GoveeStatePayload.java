package com.Tapr.Trackpad_Controller.DataTransferObject.GetDeviceState;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoveeStatePayload {
    private String sku;
    private String device;
}
