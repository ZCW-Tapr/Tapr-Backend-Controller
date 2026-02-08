package com.Tapr.Trackpad_Controller.DataTransferObject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoveeControlCapability {

    private String type;
    private String instance;

    //Using Object to handle different types
    private Object value;
}
