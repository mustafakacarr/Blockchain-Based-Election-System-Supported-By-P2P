package com.election.votify.repositories;

import com.election.votify.entities.CandidateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidateRepository extends JpaRepository<CandidateEntity, Long> {
}