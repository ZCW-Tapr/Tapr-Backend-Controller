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

import java.util.*;
import java.util.concurrent.*;

@Service
public class GestureExecutionService {

    private final GestureRuleRepository gestureRuleRepository;
    private final GoveeApiService goveeApiService;

    // In-memory cache of last-known state for toggle operations.
    private final Map<String, Integer> lastKnownState = new ConcurrentHashMap<>();

    // Timeout per device command (covers state query + control call combined)
    private static final long PER_DEVICE_TIMEOUT_MS = 5000;

    // Staggered start delay between launching each parallel device call
    private static final long STAGGER_DELAY_MS = 100;

    private int colorIndex = 0;
    private final List<Integer> colorList = List.of(
            16711680, 16728064, 16744448, 16760576, 16776960,
            12582656, 8388352, 4259584, 65280, 65344,
            65408, 65471, 65535, 49151, 32767,
            16639, 255, 4194559, 8388863, 12517631,
            16711935, 16711871, 16711808, 16711744, 16777215
    );

    public GestureExecutionService(GestureRuleRepository gestureRuleRepository, GoveeApiService goveeApiService) {
        this.gestureRuleRepository = gestureRuleRepository;
        this.goveeApiService = goveeApiService;
    }

    private String stateKey(DeviceCommand cmd) {
        return cmd.getDevice() + ":" + cmd.getCapabilityInstance();
    }

    public void executeGesture(Integer fingerCount, String gestureType, String value) {
        // Look up the rule
        GestureRule rule = gestureRuleRepository.findByFingerCountAndGestureType(fingerCount, gestureType)
                .orElseThrow(() -> new RuntimeException("No gesture rule found for " + fingerCount + " finger " + gestureType));

        if (!rule.getEnabled()) {
            return;
        }

        List<DeviceCommand> commands = rule.getDeviceCommands();

        // Slide gestures only ever target one device — fire sequentially.
        // Tap/double-tap with multiple devices — fire in parallel with staggered starts.
        if (value != null || commands.size() == 1) {
            for (DeviceCommand command : commands) {
                executeSingleCommand(command, value);
            }
        } else {
            executeInParallel(commands);
        }
    }

    private void executeInParallel(List<DeviceCommand> commands) {
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        long gestureStart = System.currentTimeMillis();

        for (int i = 0; i < commands.size(); i++) {
            DeviceCommand command = commands.get(i);

            // Stagger launch by 100ms per device for slight visual cascade
            if (i > 0) {
                try {
                    Thread.sleep(STAGGER_DELAY_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }

            CompletableFuture<Void> future = CompletableFuture
                    .runAsync(() -> executeSingleCommand(command, null))
                    .orTimeout(PER_DEVICE_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                    .exceptionally(ex -> {
                        if (ex.getCause() instanceof TimeoutException) {
                            System.out.println("[TIMEOUT] Device " + command.getDevice()
                                    + " (" + command.getCapabilityInstance()
                                    + ") exceeded " + PER_DEVICE_TIMEOUT_MS + "ms — skipped");
                        } else {
                            System.out.println("[ERROR] Device " + command.getDevice()
                                    + " failed: " + ex.getMessage());
                        }
                        return null;
                    });

            futures.add(future);
        }

        // Wait for all to complete (or timeout)
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        long totalElapsed = System.currentTimeMillis() - gestureStart;
        System.out.println("[GESTURE] Total parallel execution time: " + totalElapsed + "ms");
    }

    private void executeSingleCommand(DeviceCommand command, String value) {
        GoveeControlCapability capability = new GoveeControlCapability();
        capability.setType(command.getCapabilityType());
        capability.setInstance(command.getCapabilityInstance());

        if (value != null) {
            // Slide gesture path
            if ("brightness".equals(command.getCapabilityInstance())) {
                calculateNewBrightness(value, command, capability);
            } else if ("colorRgb".equals(command.getCapabilityInstance())) {
                cycleColor(value, capability);
            }
        } else {
            // Tap gesture path — toggle logic
            String storedValue = command.getValue();
            if (storedValue.equals("0") || storedValue.equals("1")) {
                String key = stateKey(command);
                Integer cachedValue = lastKnownState.get(key);
                int currentValue;

                if (cachedValue != null) {
                    currentValue = cachedValue;
                    System.out.println("Cache hit for " + key + " — current value: " + currentValue);
                } else {
                    System.out.println("Cache miss for " + key + " — querying state");
                    GoveeStateRequest stateRequest = new GoveeStateRequest();
                    stateRequest.setRequestId(UUID.randomUUID().toString());
                    GoveeStatePayload statePayload = new GoveeStatePayload();
                    statePayload.setSku(command.getSku());
                    statePayload.setDevice(command.getDevice());
                    stateRequest.setPayload(statePayload);

                    GoveeResponse stateResponse = goveeApiService.getDeviceState(stateRequest);
                    currentValue = 0;
                    for (CapabilityData cap : stateResponse.getPayload().getCapabilities()) {
                        if (cap.getInstance().equals(command.getCapabilityInstance())) {
                            try {
                                currentValue = cap.getState().get("value").asInt();
                            } catch (Exception e) {
                                currentValue = 0;
                            }
                            break;
                        }
                    }
                }

                int newValue = currentValue == 1 ? 0 : 1;
                capability.setValue(newValue);
                // Cache update moved to after successful Govee response (see below)
            } else {
                capability.setValue(Integer.parseInt(storedValue));
            }
        }

        // Build and send the control request
        GoveeControlPayload payload = new GoveeControlPayload();
        payload.setSku(command.getSku());
        payload.setDevice(command.getDevice());
        payload.setCapability(capability);

        GoveeControlRequest request = new GoveeControlRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setPayload(payload);

        System.out.println("Sending command to device: " + command.getSku() + " " + command.getDevice() + " value: " + capability.getValue());
        GoveeResponse response = goveeApiService.controlDevice(request);
        System.out.println("Govee response: " + response);

        // Cache update only happens here, after Govee confirms the call succeeded.
        // If the call threw (timeout, error), we never reach this line and cache stays clean.
        if (value == null && capability.getValue() instanceof Integer intValue
                && (intValue == 0 || intValue == 1)) {
            lastKnownState.put(stateKey(command), intValue);
        }
    }

    private void calculateNewBrightness(String value, DeviceCommand command, GoveeControlCapability capability) {
        GoveeStateRequest stateRequest = new GoveeStateRequest();
        stateRequest.setRequestId(UUID.randomUUID().toString());
        GoveeStatePayload statePayload = new GoveeStatePayload();
        statePayload.setSku(command.getSku());
        statePayload.setDevice(command.getDevice());
        stateRequest.setPayload(statePayload);

        GoveeResponse stateResponse = goveeApiService.getDeviceState(stateRequest);

        int currentBrightness = 50;
        if (stateResponse.getPayload() != null
                && stateResponse.getPayload().getCapabilities() != null) {
            for (var cap : stateResponse.getPayload().getCapabilities()) {
                if ("brightness".equals(cap.getInstance())) {
                    try {
                        currentBrightness = cap.getState().get("value").intValue();
                    } catch (Exception e) {
                        currentBrightness = 50;
                    }
                    break;
                }
            }
        }

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