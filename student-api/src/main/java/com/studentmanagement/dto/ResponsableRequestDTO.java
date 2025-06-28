
package com.studentmanagement.dto;

import lombok.Data;

@Data
public class ResponsableRequestDTO {
    private String prenom;
    private String nom;
    private String email;
    private String motDePasse;
}
