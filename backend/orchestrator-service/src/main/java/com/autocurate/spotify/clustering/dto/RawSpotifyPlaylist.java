package com.autocurate.spotify.clustering.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RawSpotifyPlaylist(
        String id,
        String name,
        String description,
        Owner owner,
        List<Image> images,
        @JsonProperty("external_urls") ExternalUrls externalUrls,
        Tracks tracks) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Owner(@JsonProperty("display_name") String displayName) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Image(String url) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ExternalUrls(String spotify) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Tracks(List<Item> items) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Item(Track track) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Track(
            String id,
            String name,
            List<Artist> artists,
            Album album,
            @JsonProperty("duration_ms") Integer durationMs) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Artist(String name) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Album(String name, List<Image> images) {
    }
}