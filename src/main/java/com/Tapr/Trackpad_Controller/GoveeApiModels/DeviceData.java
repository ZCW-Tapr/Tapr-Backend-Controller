package com.Tapr.Trackpad_Controller.GoveeApiModels;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceData {
    private String sku;
    private String device;
    private String deviceName;
    private String type;
    private List<CapabilityData> capabilities;
}