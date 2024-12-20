package com.election.votify.websocket;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Message {
    private String type;
    private Object data;

    @JsonCreator
    public Message(@JsonProperty("type") String type, @JsonProperty("data") Object data) {
        this.type = type;
        this.data = data;
    }
}
