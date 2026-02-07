package com.Tapr.Trackpad_Controller.Services;

import com.Tapr.Trackpad_Controller.GoveeApiModels.GoveeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class GoveeApiService {

    public final RestClient restClient;

    @Value("${govee.api.key}")
    private String apiKey;

    private static final String GOVEE_BASE_URL =  "https://openapi.api.govee.com/router/api/v1";

    public GoveeApiService(RestClient restClient) {
        this.restClient = restClient;
    }

    public GoveeResponse getDevices() {
        return restClient.get()
                .uri(GOVEE_BASE_URL + "/user/devices")
                .header("Govee-API-Key", apiKey)
                .header("Content-Type", "application/json")
                .retrieve()
                .body(GoveeResponse.class);
    }
}
