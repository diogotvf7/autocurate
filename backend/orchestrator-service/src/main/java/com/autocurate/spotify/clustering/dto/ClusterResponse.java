package com.autocurate.spotify.clustering.dto;

import java.util.List;
import java.util.Map;

public record ClusterResponse(
                String status,
                String message,
                Map<String, ClusterData> clusters) {
        public record ClusterData(
                        List<String> topTags,
                        List<String> tracks) {
        }
}
