package com.autocurate.spotify.clustering.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;

@Configuration
public class SpotifyConfig {

    @Autowired
    private SpotifyProperties spotifyProperties;

    @Bean
    public SpotifyApi spotifyApi() {
        if (spotifyProperties.getClientId() == null || spotifyProperties.getClientId().isEmpty()) {
            throw new RuntimeException("CRITICAL ERROR: Spotify Client ID is missing from environment!");
        }
        if (spotifyProperties.getClientSecret() == null || spotifyProperties.getClientSecret().isEmpty()) {
            throw new RuntimeException("CRITICAL ERROR: Spotify Client Secret is missing from environment!");
        }
        if (spotifyProperties.getRedirectUri() == null || spotifyProperties.getRedirectUri().isEmpty()) {
            throw new RuntimeException("CRITICAL ERROR: Spotify Redirect URI is missing from environment!");
        }

        return new SpotifyApi.Builder()
                .setClientId(spotifyProperties.getClientId())
                .setClientSecret(spotifyProperties.getClientSecret())
                .setRedirectUri(SpotifyHttpManager.makeUri(spotifyProperties.getRedirectUri()))
                .build();
    }
}
