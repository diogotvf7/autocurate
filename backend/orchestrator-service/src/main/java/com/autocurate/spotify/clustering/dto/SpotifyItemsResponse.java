package com.autocurate.spotify.clustering.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SpotifyItemsResponse(
        List<PlaylistItem> items) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PlaylistItem(
            Track item) {
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Track(
                String id,
                String name,
                List<Artist> artists) {
            @JsonIgnoreProperties(ignoreUnknown = true)
            public record Artist(
                    String name) {
            }
        }
    }
}
