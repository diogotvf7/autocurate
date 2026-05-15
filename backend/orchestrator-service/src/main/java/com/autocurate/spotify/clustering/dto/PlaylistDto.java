package com.autocurate.spotify.clustering.dto;

import java.util.List;

public record PlaylistDto(
                String id,
                String name,
                String description,
                String owner,
                String imageUrl,
                String externalUrl,
                List<TrackDto> tracks) {
}