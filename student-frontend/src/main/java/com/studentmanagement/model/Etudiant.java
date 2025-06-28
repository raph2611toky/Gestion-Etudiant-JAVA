package com.studentmanagement.model;

import java.util.List;

public class Etudiant {
    private String id;
    private String matricule;
    private String prenom;
    private String nom;
    private String email;
    private String adresse;
    private String niveauId;
    private String parcoursId;
    private String photo;
    private String photo_url;
    private String niveauClasse;
    private ResponsableResponse responsable;
    private List<Note> notes;

    public Etudiant() {
    }

    public Etudiant(String id, String matricule, String prenom, String nom, String email,
            String adresse,
            String niveauId, String parcoursId, String photo, String photo_url, String niveauClasse) {
        this.id = id;
        this.matricule = matricule;
        this.prenom = prenom;
        this.nom = nom;
        this.email = email;
        this.adresse = adresse;
        this.niveauId = niveauId;
        this.parcoursId = parcoursId;
        this.photo = photo;
        this.photo_url = photo_url;
        this.niveauClasse = niveauClasse;
    }

    @Override
    public String toString() {
        return String.format("%s %s (n°: %s)", nom != null ? nom.toUpperCase() : "",
                prenom != null ? prenom : "", matricule != null ? matricule : "");
    }


    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMatricule() {
        return matricule;
    }

    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getNiveauId() {
        return niveauId;
    }

    public void setNiveauId(String niveauId) {
        this.niveauId = niveauId;
    }

    public String getParcoursId() {
        return parcoursId;
    }

    public void setParcoursId(String parcoursId) {
        this.parcoursId = parcoursId;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getphoto_url() {
        return photo_url;
    }

    public void setphoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    public String getNiveauClasse() {
        return niveauClasse;
    }

    public void setNiveauClasse(String niveauClasse) {
        this.niveauClasse = niveauClasse;
    }

    public ResponsableResponse getResponsable() {
        return responsable;
    }

    public void setResponsable(ResponsableResponse responsable) {
        this.responsable = responsable;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }
}