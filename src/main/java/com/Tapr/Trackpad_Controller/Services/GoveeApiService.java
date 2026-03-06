package com.Tapr.Trackpad_Controller.Services;

import com.Tapr.Trackpad_Controller.DataTransferObject.ControlOfDevices.GoveeControlRequest;
import com.Tapr.Trackpad_Controller.DataTransferObject.GetDeviceState.GoveeStateRequest;
import com.Tapr.Trackpad_Controller.GoveeApiModels.GoveeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class GoveeApiService {

    public final RestClient restClient;

    @Value("${govee.api.key}")
    private String apiKey;

    private static final String GOVEE_BASE_URL = "https://openapi.api.govee.com/router/api/v1";

    //Constructor
    public GoveeApiService(RestClient restClient) {
        this.restClient = restClient;
    }

    //For getting all devices
    public GoveeResponse getDevices() {
        return restClient.get()
                .uri(GOVEE_BASE_URL + "/user/devices")
                .header("Govee-API-Key", apiKey)
                .header("Content-Type", "application/json")
                .retrieve()
                .body(GoveeResponse.class);
    }


    //For controlling the devices
    public GoveeResponse controlDevice(GoveeControlRequest controlRequest) {
        return restClient.post()
                .uri(GOVEE_BASE_URL + "/device/control")
                .header("Govee-API-Key", apiKey)
                .header("Content-Type", "application/json")
                .body(controlRequest)
                .retrieve()
                .body(GoveeResponse.class);
    }

    //For Getting the state of devices
    public GoveeResponse getDeviceState(GoveeStateRequest stateRequest){
        return restClient.post()
                .uri(GOVEE_BASE_URL + "/device/state")
                .header("Govee-API-Key", apiKey)
                .header("Content-Type", "application/json")
                .body(stateRequest)
                .retrieve()
                .body(GoveeResponse.class);
    }
}
