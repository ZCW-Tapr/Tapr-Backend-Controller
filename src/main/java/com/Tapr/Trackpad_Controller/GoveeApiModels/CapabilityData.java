package com.Tapr.Trackpad_Controller.GoveeApiModels;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.databind.JsonNode;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CapabilityData {

    private String type;
    private String instance;
    private JsonNode parameters;
}
