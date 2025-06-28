package com.studentmanagement.dto;

import lombok.Data;

@Data
public class MatiereRequestDTO {
    private String nom;
    private String categorie;
    private int coefficient;
    private String niveauId;
}
