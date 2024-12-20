package com.election.votify.repositories;

import com.election.votify.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    /* Username is hashed identity number as well.*/
    UserEntity findByUsername(String username);

    @Query("SELECT u FROM UserEntity u JOIN u.authorities a WHERE a = 'ROLE_VOTER'")
    List<UserEntity> findAllByRole();

    UserEntity findBySsn(String s);
}