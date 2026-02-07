package com.Tapr.Trackpad_Controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

@SpringBootApplication
public class TrackpadControllerApplication {


	@Bean
	public RestClient restClient() {
		return RestClient.create();
	}

	public static void main(String[] args) {
		SpringApplication.run(TrackpadControllerApplication.class, args);
	}

}
