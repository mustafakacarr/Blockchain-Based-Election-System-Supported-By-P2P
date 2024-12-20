package com.election.votify.request;

import com.election.votify.enums.Roles;

import java.util.List;

public record AddUserRequest(String identityNumber, String password, List<Roles> role, String ssn, String creatorSSN) {
}
