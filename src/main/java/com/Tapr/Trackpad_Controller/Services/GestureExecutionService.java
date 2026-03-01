package com.Tapr.Trackpad_Controller.Services;

import com.Tapr.Trackpad_Controller.DataTransferObject.ControlOfDevices.GoveeControlCapability;
import com.Tapr.Trackpad_Controller.DataTransferObject.ControlOfDevices.GoveeControlPayload;
import com.Tapr.Trackpad_Controller.DataTransferObject.ControlOfDevices.GoveeControlRequest;
import com.Tapr.Trackpad_Controller.Entities.DeviceCommand;
import com.Tapr.Trackpad_Controller.Entities.GestureRule;
import com.Tapr.Trackpad_Controller.GoveeApiModels.GoveeResponse;
import com.Tapr.Trackpad_Controller.Repositories.GestureRuleRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GestureExecutionService {

    private final GestureRuleRepository gestureRuleRepository;
    private final GoveeApiService goveeApiService;

    public GestureExecutionService(GestureRuleRepository gestureRuleRepository, GoveeApiService goveeApiService) {
        this.gestureRuleRepository = gestureRuleRepository;
        this.goveeApiService = goveeApiService;
    }

    public void executeGesture(Integer fingerCount, String gestureType, String value) {

        //Look up the rule
        GestureRule rule = gestureRuleRepository.findByFingerCountAndGestureType(fingerCount, gestureType)
                .orElseThrow(() -> new RuntimeException("No gesture rule found for " + fingerCount + " finger " + gestureType));

        //Check if enabled
        if (!rule.getEnabled()) {
            return;
        }


        //Loop through device commands and fire each one
        for (DeviceCommand command : rule.getDeviceCommands()) {

            //Build the capability — use incoming value for slides, stored value for taps
            GoveeControlCapability capability = new GoveeControlCapability();
            capability.setType(command.getCapabilityType());
            capability.setInstance(command.getCapabilityInstance());

            if (value != null) {
                // Slide gesture — use the value from the Pi
                capability.setValue(Integer.parseInt(value));
            } else {
                // Tap gesture — use the value stored in the database
                capability.setValue(Integer.parseInt(command.getValue()));
            }

            //Build the payload
            GoveeControlPayload payload = new GoveeControlPayload();
            payload.setSku(command.getSku());
            payload.setDevice(command.getDevice());
            payload.setCapability(capability);

            //Build the request
            GoveeControlRequest request = new GoveeControlRequest();
            request.setRequestId(UUID.randomUUID().toString());
            request.setPayload(payload);

            //Send to Govee
            System.out.println("Sending command to device: " + command.getSku() + " " + command.getDevice() + " value: " + capability.getValue());
            GoveeResponse response = goveeApiService.controlDevice(request);

//            Used for Debugging
            System.out.println("Govee response: " + response);
        }
    }
}