package com.autocurate.spotify.clustering.model;

import java.util.List;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tracks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Track {

    @Id
    private String spotifyId;
    private String name;
    private String primaryArtist;
    private String displayArtists;

    @ElementCollection
    private List<String> tags;
}
