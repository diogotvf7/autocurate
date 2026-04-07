package com.autocurate.spotify.clustering.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.autocurate.spotify.clustering.model.Track;

public interface TrackRepository extends JpaRepository<Track, String> {
}
