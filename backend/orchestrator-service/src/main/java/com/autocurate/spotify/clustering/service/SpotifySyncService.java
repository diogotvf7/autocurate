package com.autocurate.spotify.clustering.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Service;

import com.autocurate.spotify.clustering.client.MlClient;
import com.autocurate.spotify.clustering.client.SpotifyClient;
import com.autocurate.spotify.clustering.dto.ClusterResponse;
import com.autocurate.spotify.clustering.dto.PlaylistDto;
import com.autocurate.spotify.clustering.dto.PlaylistProposalResponse;
import com.autocurate.spotify.clustering.dto.TrackDto;
import com.autocurate.spotify.clustering.model.PlaylistProposal;
import com.autocurate.spotify.clustering.model.Track;
import com.autocurate.spotify.clustering.repository.PlaylistProposalRepository;
import com.autocurate.spotify.clustering.repository.TrackRepository;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

@Service
public class SpotifySyncService {

    private final SpotifyClient spotifyClient;
    private final MlClient mlClient;
    private final SpotifyApi spotifyApi;
    private final LastFmService lastFmService;
    private final PlaylistProposalRepository proposalRepository;
    private final TrackRepository trackRepository;

    public SpotifySyncService(SpotifyClient spotifyClient, MlClient mlClient, SpotifyApi spotifyApi,
            LastFmService lastFmService, PlaylistProposalRepository proposalRepository,
            TrackRepository trackRepository) {
        this.spotifyClient = spotifyClient;
        this.mlClient = mlClient;
        this.spotifyApi = spotifyApi;
        this.lastFmService = lastFmService;
        this.proposalRepository = proposalRepository;
        this.trackRepository = trackRepository;
    }

    public void syncPlaylist(String playlistId) {
        PlaylistDto response = spotifyClient.getPlaylist(playlistId);
        List<Track> tracksToSave = new ArrayList<>();

        for (TrackDto trackDto : response.tracks()) {

            if (trackRepository.existsById(trackDto.id())) {
                continue;
            }

            List<String> tags = lastFmService.getTopTags(trackDto.primaryArtist(), trackDto.name());

            tracksToSave.add(new Track(
                    trackDto.id(),
                    trackDto.name(),
                    trackDto.primaryArtist(),
                    trackDto.displayArtists(),
                    trackDto.album(),
                    trackDto.imageUrl(),
                    trackDto.durationMs(),
                    tags));
        }

        trackRepository.saveAll(tracksToSave);
    }

    public List<PlaylistProposalResponse> getAllProposals() {
        List<PlaylistProposal> proposals = proposalRepository.findAll();

        return proposals.stream().map(proposal -> {
            List<Track> actualTracks = trackRepository.findAllById(proposal.getSpotifyTrackIds());

            List<PlaylistProposalResponse.TrackInfo> trackInfos = actualTracks.stream()
                    .map(track -> new PlaylistProposalResponse.TrackInfo(
                            track.getSpotifyId(),
                            track.getName(),
                            track.getPrimaryArtist()))
                    .toList();

            return new PlaylistProposalResponse(
                    proposal.getId(),
                    proposal.getSuggestedName(),
                    proposal.getTopTags(),
                    trackInfos);

        }).toList();
    }

    public List<PlaylistProposal> generateAndSaveListProposals() {
        System.out.println("DEBUG: Requesting clusters from ML engine...");
        ClusterResponse mlResponse = mlClient.generateClusters();

        List<PlaylistProposal> proposals = new ArrayList<>();

        for (Map.Entry<String, ClusterResponse.ClusterData> entry : mlResponse.clusters().entrySet()) {
            var clusterData = entry.getValue();

            PlaylistProposal proposal = new PlaylistProposal();
            proposal.setTopTags(clusterData.topTags());
            proposal.setSpotifyTrackIds(clusterData.tracks());
            proposal.setSuggestedName(
                    "Autocurate: " + String.join(" & ", clusterData.topTags().stream().limit(3).toList()));

            proposals.add(proposal);
        }

        return proposalRepository.saveAll(proposals);
    }

    public void createPlaylistFromProposal(String proposalId, String customName) {
        PlaylistProposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new RuntimeException("Proposal not found: " + proposalId));
        proposal.setCustomName(customName);

        try {
            String userId = spotifyApi.getCurrentUsersProfile().build().execute().getId();
            String finalPlaylistName = (customName != null && !customName.isBlank()) ? customName
                    : proposal.getSuggestedName();

            var newPlaylist = spotifyApi.createPlaylist(userId, finalPlaylistName)
                    .description("Playlist auto-generated by Autocurate based on genre tags")
                    .public_(false)
                    .build()
                    .execute();

            String[] trackUris = proposal.getSpotifyTrackIds().stream()
                    .map(id -> "spotify:track:" + id)
                    .toArray(String[]::new);

            if (trackUris.length > 0) {
                spotifyApi.addItemsToPlaylist(newPlaylist.getId(), trackUris).build().execute();
                System.out.println("SUCCESS: Created playlist [" + finalPlaylistName + "]");
            }
        } catch (IOException | ParseException | SpotifyWebApiException e) {
            throw new RuntimeException("Failed to push to Spotify: " + e.getMessage());
        }
    }
}
