package com.election.votify.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Roles implements GrantedAuthority {
    ROLE_VOTER,
    ROLE_ELECTION_MANAGER;

    @Override
    public String getAuthority() {
        return name();
    }
    }
