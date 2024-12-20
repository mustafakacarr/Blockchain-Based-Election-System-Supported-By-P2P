package com.election.votify.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "candidates")
public class CandidateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "candidate_name")
    private String candidateName;

    @Column(name = "candidate_party")
    private String candidateParty;



}
