package com.election.votify.controllers;

import com.election.votify.request.AddCandidateRequest;
import com.election.votify.response.CandidateListResponse;
import com.election.votify.services.CandidateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/candidates")
public class CandidateController {
    private final CandidateService candidateService;

    public CandidateController(CandidateService candidateService) {
        this.candidateService = candidateService;
    }
    @PostMapping

    public ResponseEntity<String> addCandidate(@RequestBody AddCandidateRequest addCandidateRequest) {
        return candidateService.addCandidate(addCandidateRequest);
    }

    @GetMapping
    public CandidateListResponse getAllCandidates() {
       return candidateService.getAllCandidates();
    }
}
