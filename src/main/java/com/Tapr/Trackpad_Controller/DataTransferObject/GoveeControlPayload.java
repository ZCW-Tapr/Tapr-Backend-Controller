package com.Tapr.Trackpad_Controller.DataTransferObject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoveeControlPayload {

    private String sku;
    private String device;
    private GoveeControlCapability capability;
}
