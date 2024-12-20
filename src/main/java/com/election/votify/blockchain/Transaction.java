package com.election.votify.blockchain;

import com.election.votify.helper.DigitalSignatureHelper;
import com.election.votify.helper.SHA256Helper;
import lombok.Data;

import java.security.*;

@Data
public class Transaction {
    private String transactionId;
    private String voterHash;
    private long candidateId;
    private long timestamp;
    private byte[] signature;
    public Transaction(String voterHash, long candidateId, PrivateKey privateKey) {
        this.transactionId = calculateHash();
        this.voterHash = voterHash;
        this.candidateId = candidateId;
        this.timestamp = System.currentTimeMillis();
        this.signature = generateSignature(privateKey);
    }

    private String calculateHash() {
        String data = voterHash + candidateId + timestamp;
        return SHA256Helper.calculateHash(data);
    }

    public byte[] generateSignature(PrivateKey privateKey) {
        String transactionHash = calculateHash();
        byte[] signature;
        try {
            signature = DigitalSignatureHelper.sign(transactionHash, privateKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return signature;
    }

    public boolean verifySignature(PublicKey publicKey) {
        String transactionHash = calculateHash();
        try {
            return DigitalSignatureHelper.verify(transactionHash, signature, publicKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}