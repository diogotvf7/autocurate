package com.autocurate.spotify.clustering.dto;

import java.util.List;
import java.util.Map;

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

    public List<Map<String, Object>> getItems() {
        return items.stream()
                .map(playlistItem -> Map.of(
                        "id", playlistItem.item.id,
                        "name", playlistItem.item.name,
                        "artists", playlistItem.item.artists.stream()
                                .map(artist -> Map.of("name", artist.name))
                                .toList()))
                .toList();
    }
}
