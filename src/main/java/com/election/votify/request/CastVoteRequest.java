package com.election.votify.request;

public record CastVoteRequest(String voterHash, long votedTo) {
}
