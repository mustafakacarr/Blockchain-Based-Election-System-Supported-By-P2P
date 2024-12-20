package com.election.votify.blockchain;


import com.election.votify.helper.SHA256Helper;
import lombok.Data;

import java.time.Instant;

@Data
public class Block {
    private int index;
    private String previousHash;
    private String hash;
    private long timestamp;
    private int nonce;
    private int difficulty;

    /* ID of the candidate which is voted by the voterHash. */
    private long votedTo;

    /* Hashed form of voterHash's identity number */
    private String voterHash;
    private Transaction transaction;

    public Block(String previousHash, int difficulty, String voterHash, long votedTo, Transaction transaction) {
        this.voterHash = voterHash;
        this.votedTo = votedTo;
        this.previousHash = previousHash;
        this.difficulty = difficulty;
        this.timestamp = Instant.now().toEpochMilli();
        this.nonce = 0;
        this.hash = calculateHash();
        this.transaction = transaction;
    }


    public String calculateHash() {
        return SHA256Helper.calculateHash(previousHash + timestamp + voterHash + votedTo + nonce);
    }

    public void mineBlock() {
        String target = new String(new char[difficulty]).replace('\0', '0');
        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
        }
        System.out.println("Block mined! Hash: " + hash + ", Nonce: " + nonce + ", Difficulty: " + difficulty);
    }
}