package com.studentmanagement.dto;

import lombok.Data;

@Data
public class StudentAverageDTO {
    private String etudiantId;
    private String matricule;
    private String prenom;
    private String nom;
    private double moyenne;
    private String admissionStatus; // "Admis" or "Non Admis"
    private String mention; // e.g., "Très Bien", "Aucune"
}