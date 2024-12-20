package com.election.votify.entities;

import com.election.votify.enums.Roles;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@Entity
@Table(name = "users")
@Data
public class UserEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Identity Number has to be stored as hashed to avoid recognizing who voter voted to. It is also used as username.
     */
    private String username;
    /** SSN is used for following traces by observers about who managers added as voter.
     *  In the terms of adding user by making up, they could be punished about rigging attempt to elections.
     *  No need to fill up that field for ordinary voters,
     *  it matters for election managers which has privilege to add new voter. */
    private String ssn;
    private String password;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    @JoinTable(name = "authorities", joinColumns = @JoinColumn(name = "user_id"))
    private List<Roles> authorities;

    @ManyToOne
    private UserEntity createdBy;


}
