package com.studentmanagement.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "notes")
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etudiant_id", nullable = false, columnDefinition = "VARCHAR(36)")
    private Etudiant etudiant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matiere_id", nullable = false, columnDefinition = "VARCHAR(36)")
    private Matiere matiere;

    @Column(nullable = false)
    private float valeur;

    @Column(nullable = false)
    private String semestre;

    @Column(nullable = false)
    private String annee;
}