package com.studentmanagement.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "etudiants")
public class Etudiant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "VARCHAR(36)")
    private String id;

    @Column(unique = true, nullable = false)
    private String matricule;

    private String prenom;
    private String nom;
    private String email;
    private String adresse;
    private String niveauClasse;
    private String photo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsable_id")
    private Responsable responsable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "niveau_id")
    private Niveau niveau;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parcours_id")
    private Parcours parcours;
}