package com.autocurate.spotify.clustering.client;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.autocurate.spotify.clustering.dto.PlaylistDto;
import com.autocurate.spotify.clustering.dto.RawSpotifyPlaylist;
import com.autocurate.spotify.clustering.dto.TrackDto;

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

        public PlaylistDto getPlaylist(String playlistId) {
                String url = String.format("%s/playlists/%s?market=PT", BASE_URL, playlistId);

                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(spotifyApi.getAccessToken());
                HttpEntity<String> entity = new HttpEntity<>(headers);

                try {
                        ResponseEntity<RawSpotifyPlaylist> response = restTemplate.exchange(
                                        url, org.springframework.http.HttpMethod.GET, entity, RawSpotifyPlaylist.class);

                        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                                RawSpotifyPlaylist raw = response.getBody();

                                List<TrackDto> trackList = new java.util.ArrayList<>();

                                if (raw.tracks() != null && raw.tracks().items() != null) {
                                        trackList = raw.tracks().items().stream()
                                                        .filter(item -> item.track() != null)
                                                        .map(item -> {
                                                                var t = item.track();
                                                                String primaryArtist = (t.artists() != null
                                                                                && !t.artists().isEmpty())
                                                                                                ? t.artists().get(0)
                                                                                                                .name()
                                                                                                : "Unknown Artist";

                                                                List<String> displayArtists = (t.artists() != null
                                                                                && !t.artists().isEmpty())
                                                                                                ? t.artists().stream()
                                                                                                                .map(a -> a.name())
                                                                                                                .collect(Collectors
                                                                                                                                .toList())
                                                                                                : java.util.Collections
                                                                                                                .singletonList("Unknown Artist");

                                                                String albumName = t.album() != null ? t.album().name()
                                                                                : "Unknown Album";

                                                                String trackImageUrl = (t.album() != null
                                                                                && t.album().images() != null
                                                                                && !t.album().images().isEmpty())
                                                                                                ? t.album().images()
                                                                                                                .get(0)
                                                                                                                .url()
                                                                                                : "";

                                                                return new TrackDto(
                                                                                t.id(), t.name(), primaryArtist,
                                                                                displayArtists,
                                                                                albumName, trackImageUrl,
                                                                                t.durationMs());
                                                        }).collect(Collectors.toList());
                                }

                                String ownerName = raw.owner() != null ? raw.owner().displayName() : "Unknown Owner";
                                String imageUrl = (raw.images() != null && !raw.images().isEmpty())
                                                ? raw.images().get(0).url()
                                                : "";
                                String externalUrl = raw.externalUrls() != null ? raw.externalUrls().spotify() : "";
                                String description = raw.description() != null ? raw.description() : "";

                                return new PlaylistDto(
                                                raw.id(), raw.name(), description, ownerName, imageUrl, externalUrl,
                                                trackList);
                        }
                } catch (RestClientException e) {
                        System.out.println("DEBUG: Failed to fetch playlist " + playlistId + ": " + e.getMessage());
                        throw new RuntimeException("Failed to fetch playlist info" + e.getMessage());
                }

                throw new RuntimeException("Failed to fetch playlist info: Empty response body");
        }

        // TODO: Use DTO instead of Map<String, String>
        public List<Map<String, String>> getUserPlaylists() {
                try {
                        var playlists = spotifyApi.getListOfCurrentUsersPlaylists().build().execute();
                        return Arrays.stream(playlists.getItems())
                                        .map(playlist -> Map.of("id", playlist.getId(),
                                                        "name", playlist.getName(),
                                                        "description",
                                                        playlist.getDescription() != null ? playlist.getDescription()
                                                                        : "",
                                                        "owner", playlist.getOwner().getDisplayName(),
                                                        "externalUrl", playlist.getExternalUrls().get("spotify"),
                                                        "imageUrl",
                                                        playlist.getImages().length > 0
                                                                        ? playlist.getImages()[0].getUrl()
                                                                        : ""))
                                        .collect(Collectors.toList());
                } catch (Exception e) {
                        throw new RuntimeException("Failed to fetch user playlists: " +
                                        e.getMessage(), e);
                }
        }
}
