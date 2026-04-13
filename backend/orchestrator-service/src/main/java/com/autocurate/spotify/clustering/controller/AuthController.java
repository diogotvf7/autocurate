package com.autocurate.spotify.clustering.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import se.michaelthelin.spotify.SpotifyApi;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

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

    @GetMapping("/callback/spotify")
    public String handleCallback(@RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "error", required = false) String error) {

        if (error != null) {
            return "Spotify returned an error: " + error;
        }

        try {
            var credentials = spotifyApi.authorizationCode(code).build().execute();

            spotifyApi.setAccessToken(credentials.getAccessToken());
            spotifyApi.setRefreshToken(credentials.getRefreshToken());

            return "Success! Token acquired.";
        } catch (Exception e) {
            return "Exception: " + e.getMessage();
        }
    }
}
