package com.studentmanagement.dto;

import lombok.Data;

@Data
public class EtudiantDTO {
    private String id;
    private String matricule;
    private String prenom;
    private String nom;
    private String email;
    private String adresse;
    private String niveauClasse;
    private String photo;
    private String photo_url;
    private String responsableId;
}