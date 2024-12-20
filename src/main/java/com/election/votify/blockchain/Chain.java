package com.election.votify.blockchain;


import com.election.votify.helper.KeyPairGeneratorHelper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Data
public class Chain {

    private List<Block> chain;
    private Set<String> votedHashes;
    private static final int FIXED_DIFFICULTY = 4; // Difficulty for whole blocks on the chain.
    @JsonIgnore
    private final KeyPairGeneratorHelper keyPairGeneratorHelper = new KeyPairGeneratorHelper();

    public Chain() {
        this.chain = new ArrayList<>();
        this.votedHashes = new HashSet<>();
        chain.add(new Block("0", FIXED_DIFFICULTY, "0", 0, null)); // Genesis Block
    }


    public List<Block> getChain() {
        return chain;
    }

    public boolean hasVoterAlreadyVoted(String voterHash) {
        return votedHashes.contains(voterHash);
    }

    public void addBlock(String voter, long votedTo) throws Exception {
        KeyPair keyPair = keyPairGeneratorHelper.generateKeyPair();
        Transaction transaction = new Transaction(voter, votedTo, keyPair.getPrivate());

        if (!transaction.verifySignature(keyPair.getPublic())) {
            throw new Exception("Transaction signature is invalid");
        }
        if (ConsensusProtocol.reachConsensus(this)) {
            Block lastBlock = chain.get(chain.size() - 1);
            Block newBlock = new Block(lastBlock.getHash(), FIXED_DIFFICULTY, voter, votedTo, transaction);
            System.out.println("Mining Block 1...");
            newBlock.mineBlock();
            chain.add(newBlock);
            votedHashes.add(voter);

        } else {
            throw new Exception("Consensus not reached. Vote is invalid!");
        }


    }

    public Block getPreviousBlock(Block block) {
        int index = block.getIndex();
        if (index > 0 && index <= chain.size()) {
            return chain.get(index - 1);
        }
        return null;
    }

    public Block getLatestBlock() {
        return chain.get(chain.size() - 1);
    }

    public void printChain() {
        for (Block block : chain) {
            System.out.println("Block:");
            System.out.println("- Previous Hash: " + block.getPreviousHash());
            System.out.println("- Hash: " + block.getHash());
            System.out.println("- Timestamp: " + block.getTimestamp());
            System.out.println("- Difficulty: " + block.getDifficulty());
            System.out.println();
        }
    }

}

