package com.election.votify.services;

import com.election.votify.entities.CandidateEntity;
import com.election.votify.repositories.CandidateRepository;
import com.election.votify.request.AddCandidateRequest;
import com.election.votify.response.CandidateListResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CandidateService {
    private final CandidateRepository candidateRepository;

    public CandidateService(CandidateRepository candidateRepository) {
        this.candidateRepository = candidateRepository;
    }

    public ResponseEntity<String> addCandidate(AddCandidateRequest addCandidateRequest) {
        CandidateEntity candidateEntity = new CandidateEntity();
        candidateEntity.setCandidateName(addCandidateRequest.candidateName());
        candidateEntity.setCandidateParty(addCandidateRequest.candidateParty());
        candidateRepository.save(candidateEntity);
        if (candidateRepository.save(candidateEntity) != null) {
            return ResponseEntity.ok("Candidate added successfully");
        } else {
            throw new RuntimeException("Candidate was not able to be added!");
        }
    }


    public CandidateEntity getCandidateById(Long candidateId) {
        return candidateRepository.findById(candidateId).orElseThrow(() -> new RuntimeException("Candidate not found " + candidateId));
    }

    public CandidateListResponse getAllCandidates() {
        List<CandidateEntity> candidateList = candidateRepository.findAll();

        return new CandidateListResponse(candidateList);
    }
}
