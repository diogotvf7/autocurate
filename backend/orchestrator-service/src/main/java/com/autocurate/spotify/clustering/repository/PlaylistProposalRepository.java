package com.autocurate.spotify.clustering.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.autocurate.spotify.clustering.model.PlaylistProposal;

public interface PlaylistProposalRepository extends JpaRepository<PlaylistProposal, String> {

}
