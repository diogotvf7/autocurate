package com.autocurate.spotify.clustering.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.autocurate.spotify.clustering.dto.ClusterResponse;

@Component
public class MlClient {
    private final RestTemplate restTemplate;
    private final String ML_API_URL = "http://127.0.0.1:8000/api/ml/cluster";

    public MlClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ClusterResponse generateClusters() {
        try {
            return restTemplate.postForObject(ML_API_URL, null, ClusterResponse.class);
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to reach Python ML Service: " + e.getMessage());
        }
    }
}
