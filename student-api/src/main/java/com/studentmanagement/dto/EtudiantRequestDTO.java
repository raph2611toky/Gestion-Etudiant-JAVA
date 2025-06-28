package com.studentmanagement.dto;

import lombok.Data;

@Data
public class EtudiantRequestDTO {
    private String matricule;
    private String prenom;
    private String nom;
    private String email;
    private String adresse;
    private String niveauId;
    private String parcoursId;
}