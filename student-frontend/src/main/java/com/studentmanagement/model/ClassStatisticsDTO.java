package com.studentmanagement.model;

import java.util.List;

public class ClassStatisticsDTO {
    private String niveauId;
    private String niveauNom;
    private double moyenneGenerale;
    private double maxMoyenne;
    private double minMoyenne;
    private List<StudentAverageDTO> topStudents;
    private List<StudentAverageDTO> allStudents;

    // Getters and Setters
    public String getNiveauId() {
        return niveauId;
    }

    public void setNiveauId(String niveauId) {
        this.niveauId = niveauId;
    }

    public String getNiveauNom() {
        return niveauNom;
    }

    public void setNiveauNom(String niveauNom) {
        this.niveauNom = niveauNom;
    }

    public double getMoyenneGenerale() {
        return moyenneGenerale;
    }

    public void setMoyenneGenerale(double moyenneGenerale) {
        this.moyenneGenerale = moyenneGenerale;
    }

    public double getMaxMoyenne() {
        return maxMoyenne;
    }

    public void setMaxMoyenne(double maxMoyenne) {
        this.maxMoyenne = maxMoyenne;
    }

    public double getMinMoyenne() {
        return minMoyenne;
    }

    public void setMinMoyenne(double minMoyenne) {
        this.minMoyenne = minMoyenne;
    }

    public List<StudentAverageDTO> getTopStudents() {
        return topStudents;
    }

    public void setTopStudents(List<StudentAverageDTO> topStudents) {
        this.topStudents = topStudents;
    }

    public List<StudentAverageDTO> getAllStudents() {
        return allStudents;
    }

    public void setAllStudents(List<StudentAverageDTO> allStudents) {
        this.allStudents = allStudents;
    }
}