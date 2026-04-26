package com.autocurate.spotify.clustering.client;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.autocurate.spotify.clustering.dto.SpotifyItemsResponse;

import se.michaelthelin.spotify.SpotifyApi;

@Component
public class SpotifyClient {

    private final RestTemplate restTemplate;
    private final SpotifyApi spotifyApi;
    private static final String BASE_URL = "https://api.spotify.com/v1";

    public SpotifyClient(RestTemplate restTemplate, SpotifyApi spotifyApi) {
        this.restTemplate = restTemplate;
        this.spotifyApi = spotifyApi;
    }

    public SpotifyItemsResponse getPlaylistItems(String playlistId) {
        String url = String.format("%s/playlists/%s/items?market=PT", BASE_URL, playlistId);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(spotifyApi.getAccessToken());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<SpotifyItemsResponse> response = restTemplate.exchange(url,
                    org.springframework.http.HttpMethod.GET, entity, SpotifyItemsResponse.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
        } catch (Exception e) {
            System.out.println(
                    "DEBUG: Failed to fetch playlist items for playlist " + playlistId + ": " + e.getMessage());
        }
        return new SpotifyItemsResponse(Collections.emptyList());

    }

    public List<Map<String, String>> getUserPlaylists() {
        try {
            var playlists = spotifyApi.getListOfCurrentUsersPlaylists().build().execute();
            return Arrays.stream(playlists.getItems())
                    .map(playlist -> Map.of("id", playlist.getId(),
                            "name", playlist.getName(),
                            "description", playlist.getDescription() != null ? playlist.getDescription()
                                    : "",
                            "owner", playlist.getOwner().getDisplayName(),
                            "externalUrl", playlist.getExternalUrls().get("spotify"),
                            "imageUrl", playlist.getImages().length > 0 ? playlist.getImages()[0].getUrl() : ""))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch user playlists: " +
                    e.getMessage(), e);
        }
    }
}
