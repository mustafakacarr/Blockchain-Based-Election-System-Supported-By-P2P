package com.election.votify.helper;


import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
@Component
public class KeyPairGeneratorHelper {

    public KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048); // Key size
        return keyGen.generateKeyPair();
    }
}