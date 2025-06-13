package com.studentmanagement.service;

import com.studentmanagement.model.Etudiant;
// import com.studentmanagement.model.ResponsableResponse;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class StudentService {
    private final RestTemplate restTemplate;
    private static final String BASE_API_URL = "http://localhost:8080/api";
    private static final String ETUDIANTS_API = BASE_API_URL + "/etudiants";
    private String jwtToken;

    public StudentService() {
        this.restTemplate = new RestTemplate();
    }

    public void setJwtToken(String token) {
        this.jwtToken = token;
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        if (jwtToken != null) {
            headers.set("Authorization", "Bearer " + jwtToken);
        }
        return headers;
    }

    public List<Etudiant> getAllEtudiants(String responsableId) {
        try {
            HttpEntity<?> request = new HttpEntity<>(createHeaders());
            ResponseEntity<Etudiant[]> response = restTemplate.exchange(
                ETUDIANTS_API + "/responsable/" + responsableId,
                HttpMethod.GET,
                request,
                Etudiant[].class
            );
            return Arrays.asList(response.getBody() != null ? response.getBody() : new Etudiant[0]);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ApiException("Aucun étudiant trouvé pour ce responsable.");
            } else if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new ApiException("Non autorisé. Veuillez vous reconnecter.");
            } else {
                throw new ApiException("Erreur lors du chargement des étudiants.", ex);
            }
        } catch (Exception ex) {
            throw new ApiException("Une erreur inattendue s'est produite.", ex);
        }
    }

    public void addEtudiant(Etudiant etudiant, String responsableId, File photo) {
        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("matricule", etudiant.getMatricule());
            body.add("prenom", etudiant.getPrenom());
            body.add("nom", etudiant.getNom());
            body.add("email", etudiant.getEmail());
            body.add("adresse", etudiant.getAdresse());
            body.add("niveauClasse", etudiant.getNiveauClasse());
            if (photo != null) {
                body.add("photo", new FileSystemResource(photo));
            }

            HttpHeaders headers = createHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

            restTemplate.postForObject(ETUDIANTS_API, request, Etudiant.class);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new ApiException("Les données de l'étudiant sont invalides.");
            } else if (ex.getStatusCode() == HttpStatus.CONFLICT) {
                throw new ApiException("Un étudiant avec ce matricule ou email existe déjà.");
            } else {
                throw new ApiException("Erreur lors de l'ajout de l'étudiant.", ex);
            }
        } catch (Exception ex) {
            throw new ApiException("Une erreur inattendue s'est produite.", ex);
        }
    }

    public void updateEtudiant(String id, Etudiant etudiant, File photo) {
        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("matricule", etudiant.getMatricule());
            body.add("prenom", etudiant.getPrenom());
            body.add("nom", etudiant.getNom());
            body.add("email", etudiant.getEmail());
            body.add("adresse", etudiant.getAdresse());
            body.add("niveauClasse", etudiant.getNiveauClasse());
            if (photo != null) {
                body.add("photo", new FileSystemResource(photo));
            }

            HttpHeaders headers = createHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

            restTemplate.put(ETUDIANTS_API + "/" + id, request);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ApiException("L'étudiant avec l'ID spécifié n'existe pas.");
            } else if (ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new ApiException("Les données de mise à jour sont invalides.");
            } else {
                throw new ApiException("Erreur lors de la mise à jour de l'étudiant.", ex);
            }
        } catch (Exception ex) {
            throw new ApiException("Une erreur inattendue s'est produite.", ex);
        }
    }

    public void updatePhoto(String id, File photo) {
        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("photo", new FileSystemResource(photo));

            HttpHeaders headers = createHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

            restTemplate.put(ETUDIANTS_API + "/" + id + "/photo", request);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ApiException("L'étudiant avec l'ID spécifié n'existe pas.");
            } else {
                throw new ApiException("Erreur lors de la mise à jour de la photo.", ex);
            }
        } catch (Exception ex) {
            throw new ApiException("Une erreur inattendue s'est produite.", ex);
        }
    }

    public void deleteEtudiant(String id) {
        try {
            HttpEntity<?> request = new HttpEntity<>(createHeaders());
            restTemplate.exchange(ETUDIANTS_API + "/" + id, HttpMethod.DELETE, request, Void.class);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ApiException("L'étudiant avec l'ID spécifié n'existe pas.");
            } else {
                throw new ApiException("Erreur lors de la suppression de l'étudiant.", ex);
            }
        } catch (Exception ex) {
            throw new ApiException("Une erreur inattendue s'est produite.", ex);
        }
    }
}