package com.studentmanagement.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "parcours", uniqueConstraints = @UniqueConstraint(columnNames = { "nom", "mention_id" }))
public class Parcours {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "VARCHAR(36)")
    private String id;

    @Column(nullable = false)
    private String nom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mention_id", nullable = false, columnDefinition = "VARCHAR(36)")
    private Mention mention;

    private String description;
}