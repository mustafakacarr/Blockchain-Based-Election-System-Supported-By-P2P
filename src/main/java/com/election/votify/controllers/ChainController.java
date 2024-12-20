package com.election.votify.controllers;

import com.election.votify.blockchain.Block;
import com.election.votify.request.CastVoteRequest;
import com.election.votify.services.VoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chain")
public class ChainController {
    private final VoteService voteService;

    public ChainController(VoteService voteService) {
        this.voteService = voteService;
    }

    @PostMapping
    public ResponseEntity<String> addBlock(@RequestBody CastVoteRequest addBlockRequest) throws Exception {
       return voteService.vote(addBlockRequest);
    }

    @GetMapping
    public List<Block> getChain() {
        return voteService.getChain();
    }
}
