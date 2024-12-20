package com.election.votify.response;

import com.election.votify.entities.CandidateEntity;
import lombok.Data;

@Data
public class CandidateResponse {
    private long candidateId;
    private String candidateName;
    private String candidateParty;

    public CandidateResponse(CandidateEntity candidateEntity) {
        this.candidateId = candidateEntity.getId();
        this.candidateName = candidateEntity.getCandidateName();
        this.candidateParty = candidateEntity.getCandidateParty();
    }
}
