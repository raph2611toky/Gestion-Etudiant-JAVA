package com.studentmanagement.model;

public class Matiere {
    private String id;
    private String nom;
    private String categorie;
    private int coefficient;
    private String niveauId;

    public Matiere() {
    }

    public Matiere(String id, String nom, String categorie, int coefficient, String niveauId) {
        this.id = id;
        this.nom = nom;
        this.categorie = categorie;
        this.coefficient = coefficient;
        this.niveauId = niveauId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public int getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(int coefficient) {
        this.coefficient = coefficient;
    }

    public String getNiveauId() {
        return niveauId;
    }

    public void setNiveauId(String niveauId) {
        this.niveauId = niveauId;
    }

    @Override
    public String toString() {
        return nom;
    }
}