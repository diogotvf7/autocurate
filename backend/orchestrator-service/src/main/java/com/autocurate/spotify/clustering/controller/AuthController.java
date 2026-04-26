package com.autocurate.spotify.clustering.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.autocurate.spotify.clustering.config.AppProperties;

import se.michaelthelin.spotify.SpotifyApi;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "#{@appProperties.frontendUrl}", allowCredentials = "true")
public class AuthController {

    @Autowired
    private AppProperties appProperties;

    @Autowired
    private SpotifyApi spotifyApi;

    @GetMapping("/login")
    public RedirectView getSpotifyLoginUrl() {
        String uri = spotifyApi.authorizationCodeUri()
                .scope("user-read-private,user-read-email,playlist-read-private,playlist-read-collaborative,playlist-modify-public,playlist-modify-private")
                .show_dialog(true)
                .build()
                .execute()
                .toString();
        return new RedirectView(uri);
    }

    @GetMapping("/logout")
    public RedirectView logout() {
        spotifyApi.setAccessToken(null);
        spotifyApi.setRefreshToken(null);
        return new RedirectView(appProperties.getFrontendUrl());
    }

    @GetMapping("/callback/spotify")
    public RedirectView handleCallback(@RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "error", required = false) String error) {

        if (error != null) {
            return new RedirectView(appProperties.getFrontendUrl() + "?error=" + error);
        }

        try {
            var credentials = spotifyApi.authorizationCode(code).build().execute();

            spotifyApi.setAccessToken(credentials.getAccessToken());
            spotifyApi.setRefreshToken(credentials.getRefreshToken());

            return new RedirectView(appProperties.getFrontendUrl());
        } catch (Exception e) {
            return new RedirectView(appProperties.getFrontendUrl() + "?error=callback_failed");
        }
    }

    @GetMapping("/me/profile")
    public ResponseEntity<?> getUserProfile() {
        try {
            var profile = spotifyApi.getCurrentUsersProfile().build().execute();
            return ResponseEntity.ok(Map.of(
                    "name", profile.getDisplayName(),
                    "id", profile.getId(),
                    "image", profile.getImages()[0].getUrl()));
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
    }
}
