package com.autocurate.spotify.clustering.model;

import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "playlist_proposals")
@Data
@NoArgsConstructor
public class PlaylistProposal {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String suggestedName;
    private String customName; // Optional user-provided name

    @ElementCollection
    private List<String> topTags;

    @ElementCollection
    @CollectionTable(name = "proposal_tracks", joinColumns = @JoinColumn(name = "proposal_id"))
    @Column(name = "spotify_track_id")
    private List<String> spotifyTrackIds;

    private String userId;
}
