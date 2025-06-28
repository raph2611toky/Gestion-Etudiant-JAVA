package com.studentmanagement.model;

public class Parcours {
    private String id;
    private String nom;
    private String mentionId;
    private String description;

    public Parcours() {
    }

    public Parcours(String id, String nom, String mentionId, String description) {
        this.id = id;
        this.nom = nom;
        this.mentionId = mentionId;
        this.description = description;
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

    public String getMentionId() {
        return mentionId;
    }

    public void setMentionId(String mentionId) {
        this.mentionId = mentionId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return nom;
    }
}