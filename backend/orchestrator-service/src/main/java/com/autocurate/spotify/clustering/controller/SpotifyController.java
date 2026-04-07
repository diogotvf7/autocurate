package com.autocurate.spotify.clustering.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autocurate.spotify.clustering.client.SpotifyClient;
import com.autocurate.spotify.clustering.service.SpotifySyncService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;

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
    
    
}
