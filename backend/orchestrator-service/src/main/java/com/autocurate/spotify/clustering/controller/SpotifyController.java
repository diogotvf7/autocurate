package com.autocurate.spotify.clustering.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.autocurate.spotify.clustering.client.SpotifyClient;
import com.autocurate.spotify.clustering.dto.PlaylistProposalResponse;
import com.autocurate.spotify.clustering.model.PlaylistProposal;
import com.autocurate.spotify.clustering.service.SpotifySyncService;

@RestController
@RequestMapping("/api/spotify")
public class SpotifyController {

    @Autowired
    private SpotifyClient spotifyClient;
    @Autowired
    private SpotifySyncService spotifySyncService;

    @GetMapping("/playlists")
    public List<Map<String, String>> getUserPlaylists() {
        return spotifyClient.getUserPlaylists();
    }

    @PostMapping("/sync/{playlistId}")
    public String postMethodName(@PathVariable String playlistId) {
        spotifySyncService.syncPlaylist(playlistId);
        return "Sync triggered for playlist: " + playlistId;
    }

    @GetMapping("/proposals")
    public List<PlaylistProposalResponse> getProposals() {
        return spotifySyncService.getAllProposals();
    }

    @PostMapping("/proposals/generate")
    public ResponseEntity<List<PlaylistProposal>> generateProposals(@RequestParam(defaultValue = "5") int nClusters) {
        List<PlaylistProposal> proposals = spotifySyncService.generateAndSaveListProposals(nClusters);
        return ResponseEntity.ok(proposals);
    }

    @PostMapping("/proposals/{id}/create")
    public ResponseEntity<String> createPlaylist(@PathVariable String id,
            @RequestParam(required = false) String customName) {
        spotifySyncService.createPlaylistFromProposal(id, customName);

        return ResponseEntity.ok("Playlist created successfully on Spotify.");
    }
}
