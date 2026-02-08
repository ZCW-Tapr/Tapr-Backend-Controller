package com.Tapr.Trackpad_Controller.Controllers;

import com.Tapr.Trackpad_Controller.DataTransferObject.GoveeControlRequest;
import com.Tapr.Trackpad_Controller.GoveeApiModels.GoveeResponse;
import com.Tapr.Trackpad_Controller.Services.GoveeApiService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/govee")
public class GoveeController {

    private final GoveeApiService goveeApiService;

    public GoveeController(GoveeApiService goveeApiService) {
        this.goveeApiService = goveeApiService;
    }

    @GetMapping
    GoveeResponse getDevices(){
        return goveeApiService.getDevices();
    }

    @PostMapping ("/control")
    GoveeResponse controlDevice(@RequestBody GoveeControlRequest controlRequest){
        return goveeApiService.controlDevice(controlRequest);
    }

}
