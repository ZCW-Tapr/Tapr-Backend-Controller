package com.Tapr.Trackpad_Controller.Services;

import com.Tapr.Trackpad_Controller.DataTransferObject.ControlOfDevices.GoveeControlRequest;
import com.Tapr.Trackpad_Controller.Entities.DeviceCommand;
import com.Tapr.Trackpad_Controller.Entities.GestureRule;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GestureRuleExecutionService {

    private GestureRule receiveGestureEvent;
    private GestureRule gestureRuleLookUp;
    private Boolean enabled;
    private List<DeviceCommand> deviceCommandList;
    private DeviceCommand deviceCommand;
    private GoveeControlRequest goveeControlRequest;
    private GoveeApiService goveeApiService;
}
