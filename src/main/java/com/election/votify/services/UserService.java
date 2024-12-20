package com.election.votify.services;

import com.election.votify.entities.UserEntity;
import com.election.votify.helper.SHA256Helper;
import com.election.votify.repositories.UserRepository;
import com.election.votify.request.AddUserRequest;
import com.election.votify.security.PasswordEncoder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserEntity getByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public ResponseEntity<String> addUser(AddUserRequest addUserRequest) {
        UserEntity userEntity = fillUserEntityFieldsByAddUserRequest(addUserRequest);
        if (userRepository.save(userEntity) != null) {
            return ResponseEntity.ok("User added successfully");
        } else throw new RuntimeException("User was not able to be added!");
    }

    public UserEntity fillUserEntityFieldsByAddUserRequest(AddUserRequest addUserRequest) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(SHA256Helper.calculateHash(addUserRequest.identityNumber()));
        userEntity.setSsn(addUserRequest.ssn());
        userEntity.setPassword(passwordEncoder.bCryptPasswordEncoder().encode(addUserRequest.password()));
        userEntity.setAuthorities(addUserRequest.role());
        userEntity.setAccountNonExpired(true);
        userEntity.setAccountNonLocked(true);
        userEntity.setCredentialsNonExpired(true);
        userEntity.setEnabled(true);
        userEntity.setCreatedBy(userRepository.findBySsn(addUserRequest.creatorSSN()));

        return userEntity;
    }
}
