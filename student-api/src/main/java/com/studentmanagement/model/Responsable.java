package com.studentmanagement.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

@Data
@Entity
@Table(name = "responsables")
public class Responsable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String prenom;
    private String nom;
    private String email;

    @Column(nullable = false)
    private String motDePasse;

    @OneToMany(mappedBy = "responsable", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Etudiant> etudiants;

    // Méthode pour hacher le mot de passe avant de sauvegarder
    public void setMotDePasse(String motDePasse) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        this.motDePasse = encoder.encode(motDePasse);
    }
}