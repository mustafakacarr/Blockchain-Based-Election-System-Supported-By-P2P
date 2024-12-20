package com.election.votify.controllers;

import com.election.votify.request.CastVoteRequest;
import com.election.votify.response.ElectionResultResponse;
import com.election.votify.services.VoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/votes")
public class VoteController {

    private final VoteService voteService;

    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    @PostMapping
    public ResponseEntity<String> vote(@RequestBody CastVoteRequest addBlockRequest) throws Exception {
        return voteService.vote(addBlockRequest);
    }
    @GetMapping
    public ElectionResultResponse getElectionResult() {
        return voteService.getElectionResult();
    }

    @GetMapping("/check")
    public ResponseEntity<Boolean> checkIfUsedRightOfVote(@RequestParam String voterHash) {
        return ResponseEntity.ok(voteService.checkIfUserUsedVoteRight(voterHash));
    }

}
