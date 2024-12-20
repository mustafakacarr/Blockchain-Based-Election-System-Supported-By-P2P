package com.election.votify.response;

import com.election.votify.entities.CandidateEntity;
import lombok.Data;

import java.util.List;

@Data
public class CandidateListResponse {
    private List<CandidateResponse> candidateList;

    public CandidateListResponse(List<CandidateEntity> candidateList) {
        this.candidateList = candidateList.stream().map(CandidateResponse::new).toList();
    }
}
