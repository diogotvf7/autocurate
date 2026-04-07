package com.autocurate.spotify.clustering.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.autocurate.spotify.clustering.client.SpotifyClient;
import com.autocurate.spotify.clustering.dto.SpotifyItemsResponse;
import com.autocurate.spotify.clustering.model.Track;
import com.autocurate.spotify.clustering.repository.TrackRepository;

@Service
public class SpotifySyncService {

    private final SpotifyClient spotifyClient;
    private final LastFmService lastFmService;
    private final TrackRepository trackRepository;

    public SpotifySyncService(SpotifyClient spotifyClient, LastFmService lastFmService,
            TrackRepository trackRepository) {
        this.spotifyClient = spotifyClient;
        this.lastFmService = lastFmService;
        this.trackRepository = trackRepository;
    }

    public void syncPlaylist(String playlistId) {
        SpotifyItemsResponse response = spotifyClient.getPlaylistItems(playlistId);
        List<Track> tracksToSave = new ArrayList<>();

        for (SpotifyItemsResponse.PlaylistItem playlistItem : response.items()) {
            SpotifyItemsResponse.PlaylistItem.Track track = playlistItem.item();

            if (trackRepository.existsById(track.id()))
                continue; // Skip if track already exists

            String artistName = track.artists().isEmpty() ? "Unknown" : track.artists().get(0).name();
            List<String> tags = lastFmService.getTopTags(artistName, track.name());

            tracksToSave.add(new Track(track.id(), track.name(), artistName, tags));
        }

        trackRepository.saveAll(tracksToSave);
    }
}
