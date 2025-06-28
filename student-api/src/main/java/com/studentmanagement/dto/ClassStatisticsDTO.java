package com.studentmanagement.dto;

import lombok.Data;
import java.util.List;

@Data
public class ClassStatisticsDTO {
    private String niveauId;
    private String niveauNom;
    private double moyenneGenerale;
    private double maxMoyenne;
    private double minMoyenne;
    private List<StudentAverageDTO> topStudents;
    private List<StudentAverageDTO> allStudents;
}