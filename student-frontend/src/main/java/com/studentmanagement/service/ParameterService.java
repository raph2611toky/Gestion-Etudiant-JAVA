package com.studentmanagement.service;

import com.studentmanagement.model.Mention;
import com.studentmanagement.model.Niveau;
import com.studentmanagement.model.Parcours;
import com.studentmanagement.model.Matiere;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

// DTO classes to match backend expectations
class MentionRequestDTO {
    private String name;
    private String description;

    public MentionRequestDTO(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

class ParcoursRequestDTO {
    private String name;
    private String mentionId;
    private String description;

    public ParcoursRequestDTO(String name, String mentionId, String description) {
        this.name = name;
        this.mentionId = mentionId;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}

class MatiereRequestDTO {
    private String nom;
    private String categorie;
    private int coefficient;
    private String niveauId;

    public MatiereRequestDTO(String nom, String categorie, int coefficient, String niveauId) {
        this.nom = nom;
        this.categorie = categorie;
        this.coefficient = coefficient;
        this.niveauId = niveauId;
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
}

public class ParameterService {
    private final RestTemplate restTemplate;
    private static final String BASE_API_URL = "http://localhost:8080/api";
    private static final String MENTIONS_API = BASE_API_URL + "/mentions";
    private static final String NIVEAUX_API = BASE_API_URL + "/niveaux";
    private static final String PARCOURS_API = BASE_API_URL + "/parcours";
    private static final String MATIERES_API = BASE_API_URL + "/matieres";
    private String jwtToken;

    public ParameterService() {
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
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    // Mention CRUD Operations
    public List<Mention> getAllMentions() {
        try {
            HttpEntity<?> request = new HttpEntity<>(createHeaders());
            ResponseEntity<Mention[]> response = restTemplate.exchange(
                    MENTIONS_API,
                    HttpMethod.GET,
                    request,
                    Mention[].class);
            return Arrays.asList(response.getBody() != null ? response.getBody() : new Mention[0]);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new ApiException("Non autorisé. Veuillez vous reconnecter.");
            }
            throw new ApiException("Erreur lors du chargement des mentions: " + ex.getResponseBodyAsString());
        } catch (Exception ex) {
            throw new ApiException("Une erreur inattendue s'est produite.", ex);
        }
    }

    public void addMention(Mention mention) {
        try {
            MentionRequestDTO requestDTO = new MentionRequestDTO(mention.getNom(), mention.getDescription());
            HttpEntity<MentionRequestDTO> request = new HttpEntity<>(requestDTO, createHeaders());
            restTemplate.postForObject(MENTIONS_API, request, Mention.class);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new ApiException("Les données de la mention sont invalides.");
            } else if (ex.getStatusCode() == HttpStatus.CONFLICT) {
                throw new ApiException("Une mention avec ce nom existe déjà.");
            }
            throw new ApiException("Erreur lors de l'ajout de la mention: " + ex.getResponseBodyAsString());
        } catch (Exception ex) {
            throw new ApiException("Une erreur inattendue s'est produite.", ex);
        }
    }

    public void updateMention(String id, Mention mention) {
        try {
            MentionRequestDTO requestDTO = new MentionRequestDTO(mention.getNom(), mention.getDescription());
            HttpEntity<MentionRequestDTO> request = new HttpEntity<>(requestDTO, createHeaders());
            restTemplate.put(MENTIONS_API + "/" + id, request);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ApiException("La mention avec l'ID spécifié n'existe pas.");
            } else if (ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new ApiException("Les données de mise à jour sont invalides.");
            } else if (ex.getStatusCode() == HttpStatus.CONFLICT) {
                throw new ApiException("Une mention avec ce nom existe déjà.");
            }
            throw new ApiException("Erreur lors de la mise à jour de la mention: " + ex.getResponseBodyAsString());
        } catch (Exception ex) {
            throw new ApiException("Une erreur inattendue s'est produite.", ex);
        }
    }

    public void deleteMention(String id) {
        try {
            HttpEntity<?> request = new HttpEntity<>(createHeaders());
            restTemplate.exchange(MENTIONS_API + "/" + id, HttpMethod.DELETE, request, Void.class);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ApiException("La mention avec l'ID spécifié n'existe pas.");
            }
            throw new ApiException("Erreur lors de la suppression de la mention: " + ex.getResponseBodyAsString());
        } catch (Exception ex) {
            throw new ApiException("Une erreur inattendue s'est produite.", ex);
        }
    }

    // Niveau CRUD Operations
    public List<Niveau> getAllNiveaux() {
        try {
            HttpEntity<?> request = new HttpEntity<>(createHeaders());
            ResponseEntity<Niveau[]> response = restTemplate.exchange(
                    NIVEAUX_API,
                    HttpMethod.GET,
                    request,
                    Niveau[].class);
            return Arrays.asList(response.getBody() != null ? response.getBody() : new Niveau[0]);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new ApiException("Non autorisé. Veuillez vous reconnecter.");
            }
            throw new ApiException("Erreur lors du chargement des niveaux: " + ex.getResponseBodyAsString());
        } catch (Exception ex) {
            throw new ApiException("Une erreur inattendue s'est produite.", ex);
        }
    }

    public void addNiveau(Niveau niveau) {
        try {
            MentionRequestDTO requestDTO = new MentionRequestDTO(niveau.getNom(), niveau.getDescription());
            HttpEntity<MentionRequestDTO> request = new HttpEntity<>(requestDTO, createHeaders());
            restTemplate.postForObject(NIVEAUX_API, request, Niveau.class);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new ApiException("Les données du niveau sont invalides.");
            } else if (ex.getStatusCode() == HttpStatus.CONFLICT) {
                throw new ApiException("Un niveau avec ce nom existe déjà.");
            }
            throw new ApiException("Erreur lors de l'ajout du niveau: " + ex.getResponseBodyAsString());
        } catch (Exception ex) {
            throw new ApiException("Une erreur inattendue s'est produite.", ex);
        }
    }

    public void updateNiveau(String id, Niveau niveau) {
        try {
            MentionRequestDTO requestDTO = new MentionRequestDTO(niveau.getNom(), niveau.getDescription());
            HttpEntity<MentionRequestDTO> request = new HttpEntity<>(requestDTO, createHeaders());
            restTemplate.put(NIVEAUX_API + "/" + id, request);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ApiException("Le niveau avec l'ID spécifié n'existe pas.");
            } else if (ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new ApiException("Les données de mise à jour sont invalides.");
            } else if (ex.getStatusCode() == HttpStatus.CONFLICT) {
                throw new ApiException("Un niveau avec ce nom existe déjà.");
            }
            throw new ApiException("Erreur lors de la mise à jour du niveau: " + ex.getResponseBodyAsString());
        } catch (Exception ex) {
            throw new ApiException("Une erreur inattendue s'est produite.", ex);
        }
    }

    public void deleteNiveau(String id) {
        try {
            HttpEntity<?> request = new HttpEntity<>(createHeaders());
            restTemplate.exchange(NIVEAUX_API + "/" + id, HttpMethod.DELETE, request, Void.class);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ApiException("Le niveau avec l'ID spécifié n'existe pas.");
            }
            throw new ApiException("Erreur lors de la suppression du niveau: " + ex.getResponseBodyAsString());
        } catch (Exception ex) {
            throw new ApiException("Une erreur inattendue s'est produite.", ex);
        }
    }

    // Parcours CRUD Operations
    public List<Parcours> getAllParcours() {
        try {
            HttpEntity<?> request = new HttpEntity<>(createHeaders());
            ResponseEntity<Parcours[]> response = restTemplate.exchange(
                    PARCOURS_API,
                    HttpMethod.GET,
                    request,
                    Parcours[].class);
            return Arrays.asList(response.getBody() != null ? response.getBody() : new Parcours[0]);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new ApiException("Non autorisé. Veuillez vous reconnecter.");
            }
            return List.of();
        } catch (Exception ex) {
            throw new ApiException("Une erreur inattendue s'est produite.", ex);
        }
    }

    public List<Parcours> getAllParcoursByMention(String mentionId) {
        try {
            HttpEntity<?> request = new HttpEntity<>(createHeaders());
            ResponseEntity<Parcours[]> response = restTemplate.exchange(
                    PARCOURS_API + "/mention/" + mentionId,
                    HttpMethod.GET,
                    request,
                    Parcours[].class);
            return Arrays.asList(response.getBody() != null ? response.getBody() : new Parcours[0]);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new ApiException("Non autorisé. Veuillez vous reconnecter.");
            }
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                return List.of();
            }
            throw new ApiException("Erreur lors du chargement des parcours: " + ex.getResponseBodyAsString());
        } catch (Exception ex) {
            throw new ApiException("Une erreur inattendue s'est produite.", ex);
        }
    }

    public void addParcours(Parcours parcours) {
        try {
            ParcoursRequestDTO requestDTO = new ParcoursRequestDTO(parcours.getNom(), parcours.getMentionId(), parcours.getDescription());
            HttpEntity<ParcoursRequestDTO> request = new HttpEntity<>(requestDTO, createHeaders());
            restTemplate.postForObject(PARCOURS_API, request, Parcours.class);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new ApiException("Les données du parcours sont invalides.");
            } else if (ex.getStatusCode() == HttpStatus.CONFLICT) {
                throw new ApiException("Un parcours avec ce nom existe déjà pour cette mention.");
            }
            throw new ApiException("Erreur lors de l'ajout du parcours: " + ex.getResponseBodyAsString());
        } catch (Exception ex) {
            throw new ApiException("Une erreur inattendue s'est produite.", ex);
        }
    }

    public void updateParcours(String id, Parcours parcours) {
        try {
            ParcoursRequestDTO requestDTO = new ParcoursRequestDTO(parcours.getNom(), parcours.getMentionId(), parcours.getDescription());
            HttpEntity<ParcoursRequestDTO> request = new HttpEntity<>(requestDTO, createHeaders());
            restTemplate.put(PARCOURS_API + "/" + id, request);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ApiException("Le parcours avec l'ID spécifié n'existe pas.");
            } else if (ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new ApiException("Les données de mise à jour sont invalides.");
            } else if (ex.getStatusCode() == HttpStatus.CONFLICT) {
                throw new ApiException("Un parcours avec ce nom existe déjà pour cette mention.");
            }
            throw new ApiException("Erreur lors de la mise à jour du parcours: " + ex.getResponseBodyAsString());
        } catch (Exception ex) {
            throw new ApiException("Une erreur inattendue s'est produite.", ex);
        }
    }

    public void deleteParcours(String id) {
        try {
            HttpEntity<?> request = new HttpEntity<>(createHeaders());
            restTemplate.exchange(PARCOURS_API + "/" + id, HttpMethod.DELETE, request, Void.class);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ApiException("Le parcours avec l'ID spécifié n'existe pas.");
            }
            throw new ApiException("Erreur lors de la suppression du parcours: " + ex.getResponseBodyAsString());
        } catch (Exception ex) {
            throw new ApiException("Une erreur inattendue s'est produite.", ex);
        }
    }

    // Matiere CRUD Operations
    public List<Matiere> getAllMatieres() {
        try {
            HttpEntity<?> request = new HttpEntity<>(createHeaders());
            ResponseEntity<Matiere[]> response = restTemplate.exchange(
                    MATIERES_API,
                    HttpMethod.GET,
                    request,
                    Matiere[].class);
            return Arrays.asList(response.getBody() != null ? response.getBody() : new Matiere[0]);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new ApiException("Non autorisé. Veuillez vous reconnecter.");
            }
            throw new ApiException("Erreur lors du chargement des matières: " + ex.getResponseBodyAsString());
        } catch (Exception ex) {
            throw new ApiException("Une erreur inattendue s'est produite.", ex);
        }
    }

    public List<Matiere> getAllMatieresByNiveau(String niveauId) {
        try {
            HttpEntity<?> request = new HttpEntity<>(createHeaders());
            ResponseEntity<Matiere[]> response = restTemplate.exchange(
                    MATIERES_API + "/niveau/" + niveauId,
                    HttpMethod.GET,
                    request,
                    Matiere[].class);
            return Arrays.asList(response.getBody() != null ? response.getBody() : new Matiere[0]);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new ApiException("Non autorisé. Veuillez vous reconnecter.");
            }
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                return List.of();
            }
            throw new ApiException("Erreur lors du chargement des matières: " + ex.getResponseBodyAsString());
        } catch (Exception ex) {
            throw new ApiException("Une erreur inattendue s'est produite.", ex);
        }
    }

    public void addMatiere(Matiere matiere) {
        try {
            MatiereRequestDTO requestDTO = new MatiereRequestDTO(matiere.getNom(), matiere.getCategorie(), matiere.getCoefficient(), matiere.getNiveauId());
            HttpEntity<MatiereRequestDTO> request = new HttpEntity<>(requestDTO, createHeaders());
            restTemplate.postForObject(MATIERES_API, request, Matiere.class);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new ApiException("Les données de la matière sont invalides.");
            } else if (ex.getStatusCode() == HttpStatus.CONFLICT) {
                throw new ApiException("Une matière avec ce nom existe déjà pour ce niveau.");
            }
            throw new ApiException("Erreur lors de l'ajout de la matière: " + ex.getResponseBodyAsString());
        } catch (Exception ex) {
            throw new ApiException("Une erreur inattendue s'est produite.", ex);
        }
    }

    public void updateMatiere(String id, Matiere matiere) {
        try {
            MatiereRequestDTO requestDTO = new MatiereRequestDTO(matiere.getNom(), matiere.getCategorie(), matiere.getCoefficient(), matiere.getNiveauId());
            HttpEntity<MatiereRequestDTO> request = new HttpEntity<>(requestDTO, createHeaders());
            restTemplate.put(MATIERES_API + "/" + id, request);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ApiException("La matière avec l'ID spécifié n'existe pas.");
            } else if (ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new ApiException("Les données de mise à jour sont invalides.");
            } else if (ex.getStatusCode() == HttpStatus.CONFLICT) {
                throw new ApiException("Une matière avec ce nom existe déjà pour ce niveau.");
            }
            throw new ApiException("Erreur lors de la mise à jour de la matière: " + ex.getResponseBodyAsString());
        } catch (Exception ex) {
            throw new ApiException("Une erreur inattendue s'est produite.", ex);
        }
    }

    public void deleteMatiere(String id) {
        try {
            HttpEntity<?> request = new HttpEntity<>(createHeaders());
            restTemplate.exchange(MATIERES_API + "/" + id, HttpMethod.DELETE, request, Void.class);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ApiException("La matière avec l'ID spécifié n'existe pas.");
            }
            throw new ApiException("Erreur lors de la suppression de la matière: " + ex.getResponseBodyAsString());
        } catch (Exception ex) {
            throw new ApiException("Une erreur inattendue s'est produite.", ex);
        }
    }
}