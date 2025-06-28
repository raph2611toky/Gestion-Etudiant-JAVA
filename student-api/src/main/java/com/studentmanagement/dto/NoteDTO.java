package com.studentmanagement.dto;

import lombok.Data;

@Data
public class NoteDTO {
    private String id;
    private String etudiantId;
    private String matiereId;
    private float valeur;
    private String semestre;
    private String annee;
}