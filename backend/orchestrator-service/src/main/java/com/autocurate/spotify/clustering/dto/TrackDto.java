package com.autocurate.spotify.clustering.dto;

import java.util.List;

public record TrackDto(
                String id,
                String name,
                String primaryArtist, // Last.fm API
                List<String> displayArtists, // Frontend display
                String album,
                String imageUrl,
                Integer durationMs) {
}