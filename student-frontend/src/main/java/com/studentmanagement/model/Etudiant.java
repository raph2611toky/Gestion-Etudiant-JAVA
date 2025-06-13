package com.studentmanagement.model;

public class Etudiant {
    private String id;
    private String matricule;
    private String prenom;
    private String nom;
    private String email;
    private String adresse;
    private String niveauClasse;
    private String photo;
    private String photoUrl;
    private ResponsableResponse responsable;

    public Etudiant() {
    }

    public Etudiant(String id, String matricule, String prenom, String nom, String email, String adresse, 
                    String niveauClasse, String photo, String photoUrl) {
        this.id = id;
        this.matricule = matricule;
        this.prenom = prenom;
        this.nom = nom;
        this.email = email;
        this.adresse = adresse;
        this.niveauClasse = niveauClasse;
        this.photo = photo;
        this.photoUrl = photoUrl;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getMatricule() { return matricule; }
    public void setMatricule(String matricule) { this.matricule = matricule; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getNiveauClasse() { return niveauClasse; }
    public void setNiveauClasse(String niveauClasse) { this.niveauClasse = niveauClasse; }

    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public ResponsableResponse getResponsable() { return responsable; }
    public void setResponsable(ResponsableResponse responsable) { this.responsable = responsable; }
    
}