package com.example.ProjectManagement.client;

import com.example.ProjectManagement.dto.GlobalDto.FlowStartResponseModel;
import com.example.ProjectManagement.dto.GlobalDto.TokenModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;


@Component
public class AuthServiceClient {

    private final WebClient webClient;

    public AuthServiceClient(WebClient.Builder webClientBuilder,
                             @Value("${auth.service.url}") String authServiceUrl) {
        this.webClient = webClientBuilder
                .baseUrl(authServiceUrl)
                .build();
    }

    /**
     * Calls the Auth Service /auth-service/check-token API with client_name header and token in body
     */
    public FlowStartResponseModel checkToken(String clientName, String token) {
        try {
            return webClient.post()
                    .uri("/auth-service/check-token")
                    .header("client_name", clientName)   // required header
                    .bodyValue(new TokenModel(token))    // token object in body
                    .retrieve()
                    .bodyToMono(FlowStartResponseModel.class)
                    .block(); // blocking since we're in a sync context
        } catch (Exception e) {
            // Return a failure response model if something goes wrong
            FlowStartResponseModel errorResponse = new FlowStartResponseModel();
            errorResponse.setStatus("failure");
            errorResponse.setMessage("Error calling Auth Service: " + e.getMessage());
            return errorResponse;
        }
    }
}
