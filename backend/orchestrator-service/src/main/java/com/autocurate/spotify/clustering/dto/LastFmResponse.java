package com.autocurate.spotify.clustering.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LastFmResponse {
    private Toptags toptags;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Toptags {
        private List<Tag> tag;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Tag {
        private String name;
        private int count;
    }
}
