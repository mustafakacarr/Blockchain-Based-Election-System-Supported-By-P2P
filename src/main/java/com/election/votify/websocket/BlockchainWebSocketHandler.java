package com.election.votify.websocket;

import com.election.votify.blockchain.Block;
import com.election.votify.blockchain.Chain;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class BlockchainWebSocketHandler extends TextWebSocketHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final List<WebSocketSession> peers = new ArrayList<>();
    private static final List<String> peerList = new ArrayList<>();
    private static Chain blockchain = new Chain();

    public BlockchainWebSocketHandler() {
        loadPeerListFromFile();
        loadBlockchainFromFile();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        peers.add(session);
        String peerAddress = session.getRemoteAddress().toString();

        if (!peerList.contains(peerAddress)) {
            peerList.add(peerAddress);
        }

        System.out.println("New peer connected: " + peerAddress);

        try {
            broadcastPeerList();
        } catch (Exception e) {
            System.err.println("Error broadcasting peer list: " + e.getMessage());
        }

        savePeerListToFile();
    }


    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            Message receivedMessage = objectMapper.readValue(message.getPayload(), Message.class);
            System.out.println("Received message: " + receivedMessage);

            switch (receivedMessage.getType()) {
                case "NEW_BLOCK":
                    handleNewBlock(session, receivedMessage);
                    break;
                case "REQUEST_CHAIN":
                    handleRequestChain(session);
                    break;
                case "REQUEST_PEER_LIST":
                    handleRequestPeerList(session);
                    break;
                case "PEER_LIST_UPDATE":
                    updatePeerList(receivedMessage);
                    break;
                default:
                    System.out.println("Unknown message type: " + receivedMessage.getType());
            }
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
        }
    }

    private void saveBlockchainToFile() {
        try {
            Files.write(Paths.get("blockchain.json"), objectMapper.writeValueAsBytes(blockchain));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadBlockchainFromFile() {
        try {
            byte[] jsonData = Files.readAllBytes(Paths.get("blockchain.json"));
            blockchain = objectMapper.readValue(jsonData, Chain.class);
        } catch (IOException e) {
            System.out.println("No existing blockchain found.");
        }
    }

    private void savePeerListToFile() {
        try {
            Files.write(Paths.get("peers.json"), objectMapper.writeValueAsBytes(peerList));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadPeerListFromFile() {
        try {
            byte[] jsonData = Files.readAllBytes(Paths.get("peers.json"));
            peerList.addAll(objectMapper.readValue(jsonData, List.class));
        } catch (IOException e) {
            System.out.println("No existing peer list found.");
        }
    }

    private void handleRequestPeerList(WebSocketSession session) throws Exception {
        // Peer listesi güncellenmesi için tüm peer'lara gönder
        String peerListJson = objectMapper.writeValueAsString(new Message("PEER_LIST_RESPONSE", peerList));
        session.sendMessage(new TextMessage(peerListJson));
    }

    private void updatePeerList(Message receivedMessage) {
        List<String> receivedPeerList = objectMapper.convertValue(receivedMessage.getData(), List.class);
        peerList.clear();
        peerList.addAll(receivedPeerList);
        try {
            // Peer listesi güncellenince diğer peer'lara ilet
            broadcastPeerList();
        } catch (Exception e) {
            System.err.println("Error broadcasting peer list: " + e.getMessage());
        }
    }

    private void handleNewBlock(WebSocketSession session, Message receivedMessage) throws Exception {
        Chain receivedChain = objectMapper.convertValue(receivedMessage.getData(), Chain.class);
        checkLengthAndTriggerBroadcast(receivedChain.getChain());
    }

    public void checkLengthAndTriggerBroadcast(List<Block> chain) {
        if (chain.size() > blockchain.getChain().size()) {
            blockchain.getChain().clear();
            blockchain.getChain().addAll(chain);
            saveBlockchainToFile();
        }
        broadcastAsync(new Message("CHAIN_UPDATED", blockchain));
    }

    private void handleRequestChain(WebSocketSession session) throws Exception {
        String blockchainJson = objectMapper.writeValueAsString(new Message("CHAIN_RESPONSE", blockchain));
        session.sendMessage(new TextMessage(blockchainJson));
    }

    private void broadcast(Message message) throws Exception {
        String jsonMessage = objectMapper.writeValueAsString(message);
        for (WebSocketSession peer : peers) {
            if (peer.isOpen()) {
                peer.sendMessage(new TextMessage(jsonMessage));
            }
        }
    }

    private void broadcastAsync(Message message) {
        CompletableFuture.runAsync(() -> {
            String jsonMessage = null;
            try {
                jsonMessage = objectMapper.writeValueAsString(message);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            for (WebSocketSession peer : peers) {
                if (peer.isOpen()) {
                    try {
                        peer.sendMessage(new TextMessage(jsonMessage));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void broadcastPeerList(){
        broadcastAsync(new Message("PEER_LIST_UPDATE", peerList));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) {
        peers.remove(session);
        String peerAddress = session.getRemoteAddress().toString();
        peerList.remove(peerAddress);
        System.out.println("Peer disconnected: " + session.getId());
        savePeerListToFile();
        saveBlockchainToFile();
    }
}
