package com.Tapr.Trackpad_Controller.Services;

import com.Tapr.Trackpad_Controller.DataTransferObject.ControlOfDevices.GoveeControlRequest;
import com.Tapr.Trackpad_Controller.DataTransferObject.GetDeviceState.GoveeStateRequest;
import com.Tapr.Trackpad_Controller.GoveeApiModels.GoveeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Service
public class GoveeApiService {

    public final RestClient restClient;

    @Value("${govee.api.key}")
    private String apiKey;

    private static final String GOVEE_BASE_URL = "https://openapi.api.govee.com/router/api/v1";

    // HTTP-level timeouts. These actually close the underlying socket,
    // unlike CompletableFuture.orTimeout which only marks the future.
    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(2);
    private static final Duration READ_TIMEOUT = Duration.ofSeconds(4);

    public GoveeApiService(RestClient.Builder builder) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) CONNECT_TIMEOUT.toMillis());
        factory.setReadTimeout((int) READ_TIMEOUT.toMillis());

        this.restClient = builder
                .requestFactory(factory)
                .build();
    }

    public GoveeResponse getDevices() {
        return restClient.get()
                .uri(GOVEE_BASE_URL + "/user/devices")
                .header("Govee-API-Key", apiKey)
                .header("Content-Type", "application/json")
                .retrieve()
                .body(GoveeResponse.class);
    }

    public GoveeResponse controlDevice(GoveeControlRequest controlRequest) {
        long start = System.currentTimeMillis();
        GoveeResponse response = restClient.post()
                .uri(GOVEE_BASE_URL + "/device/control")
                .header("Govee-API-Key", apiKey)
                .header("Content-Type", "application/json")
                .body(controlRequest)
                .retrieve()
                .body(GoveeResponse.class);
        long elapsed = System.currentTimeMillis() - start;
        System.out.println("[TIMING] controlDevice took " + elapsed + "ms — sku="
                + controlRequest.getPayload().getSku()
                + " device=" + controlRequest.getPayload().getDevice());
        return response;
    }

    public GoveeResponse getDeviceState(GoveeStateRequest stateRequest) {
        long start = System.currentTimeMillis();
        GoveeResponse response = restClient.post()
                .uri(GOVEE_BASE_URL + "/device/state")
                .header("Govee-API-Key", apiKey)
                .header("Content-Type", "application/json")
                .body(stateRequest)
                .retrieve()
                .body(GoveeResponse.class);
        long elapsed = System.currentTimeMillis() - start;
        System.out.println("[TIMING] getDeviceState took " + elapsed + "ms — sku="
                + stateRequest.getPayload().getSku()
                + " device=" + stateRequest.getPayload().getDevice());
        return response;
    }
}