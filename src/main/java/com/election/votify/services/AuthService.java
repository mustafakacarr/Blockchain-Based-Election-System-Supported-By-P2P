package com.election.votify.services;

import com.election.votify.entities.UserEntity;
import com.election.votify.helper.SHA256Helper;
import com.election.votify.request.AuthRequest;
import com.election.votify.response.AuthResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager, UserService userService, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    public ResponseEntity<AuthResponse> login(AuthRequest authRequest) {
        String username=SHA256Helper.calculateHash(authRequest.username());
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(username, authRequest.password()));
        if (authentication.isAuthenticated()) {
            UserEntity user = userService.getByUsername(username);
            String token = jwtService.generateToken(user);

            AuthResponse authResponse = new AuthResponse();

            authResponse.setMessage("User successfully logged in.");
            authResponse.setAccessToken("Bearer " + token);
            authResponse.setUserId(user.getId());
            authResponse.setUsername(user.getUsername());
            authResponse.setRoles(user.getAuthorities());
            authResponse.setSsn(user.getSsn());

            return new ResponseEntity<>(authResponse, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
