package com.election.votify.blockchain;

public class ConsensusProtocol {
    public static boolean reachConsensus(Chain chain) {
        Block latestBlock = chain.getLatestBlock();
        Block previousBlock = chain.getPreviousBlock(latestBlock);
        if (previousBlock == null) {
            return true;
        }
        return previousBlock.getHash().equals(latestBlock.getPreviousHash());
    }

}
