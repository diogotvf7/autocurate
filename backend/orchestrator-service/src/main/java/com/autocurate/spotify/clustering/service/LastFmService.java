package com.autocurate.spotify.clustering.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.autocurate.spotify.clustering.dto.LastFmResponse;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.List;

@Service
public class LastFmService {

    @Value("${lastfm.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final String BASE_URL = "http://ws.audioscrobbler.com/2.0/";

    public LastFmService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<String> getTopTags(String artist, String track) {
        List<String> trackTags = getTrackTags(artist, track);
        if (!trackTags.isEmpty()) {
            System.out.println("DEBUG: Using track tags for " + artist + " - " + track);
            return trackTags;
        }
        System.out.println("DEBUG: Using artist tags for " + artist);
        return getArtistTags(artist);
    }

    private List<String> getTrackTags(String artist, String track) {
        try {
            String url = UriComponentsBuilder.fromUriString(BASE_URL)
                    .queryParam("method", "track.gettoptags")
                    .queryParam("artist", artist)
                    .queryParam("track", track)
                    .queryParam("autocorrect", "1")
                    .queryParam("api_key", apiKey)
                    .queryParam("format", "json")
                    .build().toUriString();

            LastFmResponse response = restTemplate.getForObject(url, LastFmResponse.class);
            return extractTags(response);
        } catch (Exception e) {
            System.out
                    .println("DEBUG: Failed to fetch track tags for " + artist + " - " + track + ": " + e.getMessage());
            return Collections.emptyList();
        }
    }

    private List<String> getArtistTags(String artist) {
        try {
            String url = UriComponentsBuilder.fromUriString(BASE_URL)
                    .queryParam("method", "artist.gettoptags")
                    .queryParam("artist", artist)
                    .queryParam("api_key", apiKey)
                    .queryParam("format", "json")
                    .build().toUriString();

            LastFmResponse response = restTemplate.getForObject(url, LastFmResponse.class);
            return extractTags(response);
        } catch (Exception e) {
            System.out.println("DEBUG: Failed to fetch artist tags for " + artist + ": " + e.getMessage());
            return Collections.emptyList();
        }
    }

    private List<String> extractTags(LastFmResponse response) {
        if (response != null &&
                response.getToptags() != null &&
                response.getToptags().getTag() != null) {

            return response.getToptags().getTag().stream()
                    .map(LastFmResponse.Tag::getName)
                    .limit(5)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

}
