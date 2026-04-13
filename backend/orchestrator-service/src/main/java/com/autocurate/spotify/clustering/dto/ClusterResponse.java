package com.autocurate.spotify.clustering.dto;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ClusterResponse(
                String status,
                String message,
                Map<String, ClusterData> clusters) {
        public record ClusterData(
                        @JsonProperty("top_tags") List<String> topTags,
                        List<String> tracks) {
        }
}
