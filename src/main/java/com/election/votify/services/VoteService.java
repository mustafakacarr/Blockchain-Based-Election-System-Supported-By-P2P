package com.election.votify.services;

import com.election.votify.blockchain.Block;
import com.election.votify.blockchain.Chain;
import com.election.votify.entities.CandidateEntity;
import com.election.votify.repositories.UserRepository;
import com.election.votify.request.CastVoteRequest;
import com.election.votify.response.ElectionResultResponse;
import com.election.votify.response.VoteResponse;
import com.election.votify.websocket.BlockchainWebSocketHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class VoteService {
    private final Chain chain;
    private final CandidateService candidateService;
    private final UserRepository userRepository;
    private final BlockchainWebSocketHandler blockchainWebSocketHandler;

    public VoteService(Chain chain, CandidateService candidateService, UserRepository userRepository, BlockchainWebSocketHandler blockchainWebSocketHandler) {
        this.chain = chain;
        this.candidateService = candidateService;
        this.userRepository = userRepository;
        this.blockchainWebSocketHandler = blockchainWebSocketHandler;
    }

    public ResponseEntity<String> vote(CastVoteRequest addBlockRequest) throws Exception {
        if (userRepository.findByUsername(addBlockRequest.voterHash()) == null) {
            return ResponseEntity.badRequest().body("Voter not found! Rigging attempt detected!");
        }
        if (checkIfUserUsedVoteRight(addBlockRequest.voterHash())) {
            return ResponseEntity.badRequest().body("Double-Spending Attempt! You have already voted. ");
        } else {
            chain.addBlock(addBlockRequest.voterHash(), addBlockRequest.votedTo());
            blockchainWebSocketHandler.checkLengthAndTriggerBroadcast(chain.getChain());
        }
        return ResponseEntity.ok("Block added");
    }

    public boolean checkIfUserUsedVoteRight(String voterHash) {
        return chain.hasVoterAlreadyVoted(voterHash);
    }

    public List<Block> getChain() {
        return chain.getChain();
    }

    public ElectionResultResponse getElectionResult() {
        ElectionResultResponse electionResultResponse = new ElectionResultResponse();
        HashMap<Long, Integer> candidateVotes = new HashMap<>();
        List<VoteResponse> voteResponseList = new ArrayList<>();
        for (Block block : chain.getChain()) {
            if (block.getVotedTo() != 0) {
                candidateVotes.merge(block.getVotedTo(), 1, Integer::sum);
            }
        }
        for (Long candidateId : candidateVotes.keySet()) {
            CandidateEntity candidate = candidateService.getCandidateById(candidateId);
            if (candidate != null) {
                VoteResponse voteResponse = new VoteResponse(candidate, candidateVotes.get(candidateId));
                voteResponseList.add(voteResponse);
            } else
                throw new RuntimeException("Candidate not found even though he/she has votes. It might mean rigged election!");
        }
        electionResultResponse.setVoteList(voteResponseList);
        int validatedVotes = chain.getChain().size() - 1;
        int eligibleVoters = getEligibleVoters();
        if (eligibleVoters > 0) {
            int percentage = (validatedVotes * 100) / eligibleVoters;
            electionResultResponse.setCastPercentageOfVoters(percentage);
        } else {
            electionResultResponse.setCastPercentageOfVoters(0);
        }


        return electionResultResponse;

    }

    public int getEligibleVoters() {
        return userRepository.findAllByRole().size();
    }


}