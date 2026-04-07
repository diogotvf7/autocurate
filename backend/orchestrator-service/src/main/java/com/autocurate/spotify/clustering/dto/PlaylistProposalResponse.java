package com.autocurate.spotify.clustering.dto;

import java.util.List;

public record PlaylistProposalResponse(
        String id,
        String suggestedName,
        List<String> topTags,
        List<TrackInfo> tracks) {
    public record TrackInfo(
            String spotifyId,
            String name,
            String artist) {
    }
}
