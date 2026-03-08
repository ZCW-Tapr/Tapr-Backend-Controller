package com.Tapr.Trackpad_Controller.Services;

import com.Tapr.Trackpad_Controller.DataTransferObject.ControlOfDevices.GoveeControlCapability;
import com.Tapr.Trackpad_Controller.DataTransferObject.ControlOfDevices.GoveeControlPayload;
import com.Tapr.Trackpad_Controller.DataTransferObject.ControlOfDevices.GoveeControlRequest;
import com.Tapr.Trackpad_Controller.DataTransferObject.GetDeviceState.GoveeStatePayload;
import com.Tapr.Trackpad_Controller.DataTransferObject.GetDeviceState.GoveeStateRequest;
import com.Tapr.Trackpad_Controller.Entities.DeviceCommand;
import com.Tapr.Trackpad_Controller.Entities.GestureRule;
import com.Tapr.Trackpad_Controller.GoveeApiModels.CapabilityData;
import com.Tapr.Trackpad_Controller.GoveeApiModels.GoveeResponse;
import com.Tapr.Trackpad_Controller.Repositories.GestureRuleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GestureExecutionService {

    private final GestureRuleRepository gestureRuleRepository;
    private final GoveeApiService goveeApiService;
    private int colorIndex = 0;
    private final List<Integer> colorList = List.of(
            16711680,  // Red
            16728064,  // Red-Orange
            16744448,  // Orange
            16760576,  // Amber
            16776960,  // Yellow
            12582656,  // Yellow-Green
            8388352,   // Lime
            4259584,   // Chartreuse
            65280,     // Green
            65344,     // Green-Teal
            65408,     // Teal
            65471,     // Teal-Cyan
            65535,     // Cyan
            49151,     // Light Blue
            32767,     // Sky Blue
            16639,     // Azure
            255,       // Blue
            4194559,   // Blue-Violet
            8388863,   // Violet
            12517631,  // Purple
            16711935,  // Magenta
            16711871,  // Hot Pink
            16711808,  // Rose
            16711744,  // Crimson
            16777215   // White
    );


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

//            if (value != null) {
//                // Slide gesture — use the value from the Pi
//                capability.setValue(Integer.parseInt(value));
//            }
            if (value != null) {
                if ("brightness".equals(command.getCapabilityInstance())) {
                    calculateNewBrightness(value, command, capability);
                } else if ("colorRgb".equals(command.getCapabilityInstance())) {
                cycleColor(value, capability);
                }
            }
            else {
                String storedValue = command.getValue();

                // Toggle logic — query current state and send opposite
                if (storedValue.equals("0") || storedValue.equals("1")) {
                    GoveeStateRequest stateRequest = new GoveeStateRequest();
                    stateRequest.setRequestId(UUID.randomUUID().toString());
                    GoveeStatePayload statePayload = new GoveeStatePayload();
                    statePayload.setSku(command.getSku());
                    statePayload.setDevice(command.getDevice());
                    stateRequest.setPayload(statePayload);

                    GoveeResponse stateResponse = goveeApiService.getDeviceState(stateRequest);

                    int currentValue = 0;
                    for (CapabilityData cap : stateResponse.getPayload().getCapabilities()) {
                        if (cap.getInstance().equals(command.getCapabilityInstance())) {
                            try {
                                currentValue = cap.getState().get("value").asInt();
                            } catch (Exception e) {
                                currentValue = 0;
                            }                            break;
                        }
                    }

                    capability.setValue(currentValue == 1 ? 0 : 1);
                } else {
                    capability.setValue(Integer.parseInt(storedValue));
                }
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

    private void calculateNewBrightness(String value, DeviceCommand command, GoveeControlCapability capability) {
        // Step 1: Query current device state (same pattern as your toggle logic)
        GoveeStateRequest stateRequest = new GoveeStateRequest();
        stateRequest.setRequestId(UUID.randomUUID().toString());
        GoveeStatePayload statePayload = new GoveeStatePayload();
        statePayload.setSku(command.getSku());
        statePayload.setDevice(command.getDevice());
        stateRequest.setPayload(statePayload);

        GoveeResponse stateResponse = goveeApiService.getDeviceState(stateRequest);

        // Step 2: Find current brightness from the response
        int currentBrightness = 50; // default fallback
        if (stateResponse.getPayload() != null
                && stateResponse.getPayload().getCapabilities() != null) {
            for (var cap : stateResponse.getPayload().getCapabilities()) {
                if ("brightness".equals(cap.getInstance())) {
                    try {
                        currentBrightness = cap.getState().get("value").intValue();
                    } catch (Exception e) {
                        currentBrightness = 50;
                    }                    break;
                }
            }
        }

        // Step 3: Apply delta and clamp between 1-100
        int delta = Integer.parseInt(value);
        int newBrightness = Math.max(1, Math.min(100, currentBrightness + delta));

        capability.setValue(newBrightness);
    }

    private void cycleColor(String value, GoveeControlCapability capability) {
        int direction = Integer.parseInt(value);
        colorIndex = (colorIndex + direction) % colorList.size();
        if (colorIndex < 0) {
            colorIndex += colorList.size();
        }
        capability.setValue(colorList.get(colorIndex));
    }
}