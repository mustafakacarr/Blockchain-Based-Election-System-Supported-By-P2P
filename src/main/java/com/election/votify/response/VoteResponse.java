package com.election.votify.response;

import com.election.votify.entities.CandidateEntity;
import lombok.Data;

@Data
public class VoteResponse {
    private long candidateId;
    private String candidateName;
    private String candidateParty;
    private int candidateTotalVotes;

    public VoteResponse(CandidateEntity candidateEntity,int candidateTotalVotes) {
        this.candidateId = candidateEntity.getId();
        this.candidateName = candidateEntity.getCandidateName();
        this.candidateParty = candidateEntity.getCandidateParty();
        this.candidateTotalVotes = candidateTotalVotes;
    }
}
