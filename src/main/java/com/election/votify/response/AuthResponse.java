package com.election.votify.response;

import com.election.votify.enums.Roles;
import lombok.Data;

import java.util.List;

@Data
public class AuthResponse {
    private String message;
    private String accessToken;
    private Long userId;
    private String username;
    private List<Roles> roles;
    private String ssn;
}
