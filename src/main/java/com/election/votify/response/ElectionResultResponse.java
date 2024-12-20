package com.election.votify.response;

import lombok.Data;

import java.util.List;
@Data
public class ElectionResultResponse {
    private List<VoteResponse> voteList;
    private long timestamp;
    private int castPercentageOfVoters;

    public ElectionResultResponse() {
        this.timestamp = System.currentTimeMillis();
    }
}
