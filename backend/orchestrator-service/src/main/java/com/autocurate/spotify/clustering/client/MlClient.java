package com.autocurate.spotify.clustering.client;

import java.util.Map;

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

    public ClusterResponse generateClusters(int nClusters) {
        try {
            Map<String, Integer> requestBody = Map.of("n_clusters", nClusters);
            return restTemplate.postForObject(ML_API_URL, requestBody, ClusterResponse.class);
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to reach Python ML Service: " + e.getMessage());
        }
    }
}
