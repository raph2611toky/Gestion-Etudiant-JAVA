package com.studentmanagement.dto;

import lombok.Data;

@Data
public class NoteRequestDTO {
    private String etudiantId;
    private String matiereId;
    private Float valeur;
    private String semestre;
    private String annee;
}