package com.autocurate.spotify.clustering.dto;

public record TrackDto(
                String id,
                String name,
                String primaryArtist, // Last.fm API
                String displayArtists, // Frontend display
                String album,
                Integer durationMs,
                String imageUrl) {
}