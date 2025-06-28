package com.studentmanagement.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "matieres", uniqueConstraints = @UniqueConstraint(columnNames = { "nom", "niveau_id" }))
public class Matiere {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "VARCHAR(36)")
    private String id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String categorie;

    @Column(nullable = false)
    private int coefficient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "niveau_id", nullable = false, columnDefinition = "VARCHAR(36)")
    private Niveau niveau;
}