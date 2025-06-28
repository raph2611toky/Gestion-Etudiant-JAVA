package com.studentmanagement.model;

public class NoteDTO {
    private String id;
    private String etudiantId;
    private String matiereId;
    private float valeur;
    private String semestre;
    private String annee;

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEtudiantId() {
        return etudiantId;
    }

    public void setEtudiantId(String etudiantId) {
        this.etudiantId = etudiantId;
    }

    public String getMatiereId() {
        return matiereId;
    }

    public void setMatiereId(String matiereId) {
        this.matiereId = matiereId;
    }

    public float getValeur() {
        return valeur;
    }

    public void setValeur(float valeur) {
        this.valeur = valeur;
    }

    public String getSemestre() {
        return semestre;
    }

    public void setSemestre(String semestre) {
        this.semestre = semestre;
    }

    public String getAnnee() {
        return annee;
    }

    public void setAnnee(String annee) {
        this.annee = annee;
    }
}