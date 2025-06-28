package com.studentmanagement.service;

import com.studentmanagement.model.*;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.*;

public class StudentService {
    private final RestTemplate restTemplate;
    private static final String BASE_API_URL = "http://localhost:8080/api";
    private static final String ETUDIANTS_API = BASE_API_URL + "/etudiants";
    private static final String NIVEAUX_API = BASE_API_URL + "/niveaux";
    private static final String PARCOURS_API = BASE_API_URL + "/parcours";
    private static final String MENTIONS_API = BASE_API_URL + "/mentions";
    private static final String MATIERES_API = BASE_API_URL + "/matieres";
    private static final String NOTES_API = BASE_API_URL + "/notes";
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
                ETUDIANTS_API,
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

    public List<Niveau> getAllNiveaux() {
        try {
            HttpEntity<?> request = new HttpEntity<>(createHeaders());
            ResponseEntity<Niveau[]> response = restTemplate.exchange(
                NIVEAUX_API,
                HttpMethod.GET,
                request,
                Niveau[].class
            );
            return Arrays.asList(response.getBody() != null ? response.getBody() : new Niveau[0]);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new ApiException("Non autorisé. Veuillez vous reconnecter.");
            }
            throw new ApiException("Erreur lors du chargement des niveaux.", ex);
        } catch (Exception ex) {
            throw new ApiException("Une erreur inattendue s'est produite.", ex);
        }
    }

    public List<Parcours> getAllParcours() {
        try {
            HttpEntity<?> request = new HttpEntity<>(createHeaders());
            ResponseEntity<Parcours[]> response = restTemplate.exchange(
                PARCOURS_API,
                HttpMethod.GET,
                request,
                Parcours[].class
            );
            return Arrays.asList(response.getBody() != null ? response.getBody() : new Parcours[0]);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new ApiException("Non autorisé. Veuillez vous reconnecter.");
            }
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                return new ArrayList<>();
            }
            throw new ApiException("Erreur lors du chargement des parcours.", ex);
        } catch (Exception ex) {
            throw new ApiException("Une erreur inattendue s'est produite.", ex);
        }
    }

    public List<Mention> getAllMentions() {
        try {
            HttpEntity<?> request = new HttpEntity<>(createHeaders());
            ResponseEntity<Mention[]> response = restTemplate.exchange(
                MENTIONS_API,
                HttpMethod.GET,
                request,
                Mention[].class
            );
            return Arrays.asList(response.getBody() != null ? response.getBody() : new Mention[0]);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new ApiException("Non autorisé. Veuillez vous reconnecter.");
            }
            throw new ApiException("Erreur lors du chargement des mentions.", ex);
        } catch (Exception ex) {
            throw new ApiException("Une erreur inattendue s'est produite.", ex);
        }
    }

    public List<Matiere> getAllMatieres() {
        try {
            HttpEntity<?> request = new HttpEntity<>(createHeaders());
            ResponseEntity<Matiere[]> response = restTemplate.exchange(
                MATIERES_API,
                HttpMethod.GET,
                request,
                Matiere[].class
            );
            return Arrays.asList(response.getBody() != null ? response.getBody() : new Matiere[0]);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new ApiException("Non autorisé. Veuillez vous reconnecter.");
            }
            throw new ApiException("Erreur lors du chargement des matières.", ex);
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
            body.add("niveauId", etudiant.getNiveauId());
            body.add("parcoursId", etudiant.getParcoursId());
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
            body.add("niveauId", etudiant.getNiveauId());
            body.add("parcoursId", etudiant.getParcoursId());
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

    public void updateNiveauParcours(String id, String niveauId, String parcoursId) {
        try {
            HttpHeaders headers = createHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String body = "{\"niveauId\":\"" + niveauId + "\",\"parcoursId\":\"" + parcoursId + "\"}";
            HttpEntity<String> request = new HttpEntity<>(body, headers);

            restTemplate.put(ETUDIANTS_API + "/" + id + "/niveau-parcours", request);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ApiException("L'étudiant ou les ressources spécifiées n'existent pas.");
            } else if (ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new ApiException("Les données de mise à jour sont invalides.");
            } else {
                throw new ApiException("Erreur lors de la mise à jour du niveau et parcours.", ex);
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

    public List<Note> getAllNotes(String semestre, String annee) {
        try {
            StringBuilder url = new StringBuilder(NOTES_API);
            if (semestre != null || annee != null) {
                url.append("?");
                if (semestre != null) {
                    url.append("semestre=").append(semestre);
                    if (annee != null) {
                        url.append("&");
                    }
                }
                if (annee != null) {
                    url.append("annee=").append(annee);
                }
            }
            HttpEntity<?> request = new HttpEntity<>(createHeaders());
            ResponseEntity<Note[]> response = restTemplate.exchange(
                url.toString(),
                HttpMethod.GET,
                request,
                Note[].class
            );
            return Arrays.asList(response.getBody() != null ? response.getBody() : new Note[0]);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                return new ArrayList<>();
            } else if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new ApiException("Non autorisé. Veuillez vous reconnecter.");
            } else {
                throw new ApiException("Erreur lors du chargement des notes.", ex);
            }
        } catch (Exception ex) {
            throw new ApiException("Une erreur inattendue s'est produite.", ex);
        }
    }

    public List<Note> getNotesByEtudiant(String etudiantId) {
        try {
            HttpEntity<?> request = new HttpEntity<>(createHeaders());
            ResponseEntity<Note[]> response = restTemplate.exchange(
                NOTES_API + "/etudiant/" + etudiantId,
                HttpMethod.GET,
                request,
                Note[].class
            );
            return Arrays.asList(response.getBody() != null ? response.getBody() : new Note[0]);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                return new ArrayList<>();
            } else if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new ApiException("Non autorisé. Veuillez vous reconnecter.");
            } else {
                throw new ApiException("Erreur lors du chargement des notes.", ex);
            }
        } catch (Exception ex) {
            throw new ApiException("Une erreur inattendue s'est produite.", ex);
        }
    }

    public void addNote(Note note) {
        try {
            NoteDTO dto = new NoteDTO();
            dto.setEtudiantId(note.getEtudiantId());
            dto.setMatiereId(note.getMatiereId());
            dto.setValeur(note.getValeur());
            dto.setSemestre(note.getSemestre());
            dto.setAnnee(note.getAnnee());
            HttpEntity<NoteDTO> request = new HttpEntity<>(dto, createHeaders());
            restTemplate.postForObject(NOTES_API, request, NoteDTO.class);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new ApiException("Les données de la note sont invalides.");
            } else if (ex.getStatusCode() == HttpStatus.CONFLICT) {
                throw new ApiException("Une note pour cet étudiant et cette matière existe déjà.");
            }
            throw new ApiException("Erreur lors de l'ajout de la note.", ex);
        } catch (Exception ex) {
            throw new ApiException("Une erreur inattendue s'est produite.", ex);
        }
    }

    public void updateNote(String id, Note note) {
        try {
            NoteDTO dto = new NoteDTO();
            dto.setEtudiantId(note.getEtudiantId());
            dto.setMatiereId(note.getMatiereId());
            dto.setValeur(note.getValeur());
            dto.setSemestre(note.getSemestre());
            dto.setAnnee(note.getAnnee());
            HttpEntity<NoteDTO> request = new HttpEntity<>(dto, createHeaders());
            restTemplate.put(NOTES_API + "/" + id, request);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ApiException("La note avec l'ID spécifié n'existe pas.");
            } else if (ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new ApiException("Les données de mise à jour sont invalides.");
            }
            throw new ApiException("Erreur lors de la mise à jour de la note.", ex);
        } catch (Exception ex) {
            throw new ApiException("Une erreur inattendue s'est produite.", ex);
        }
    }

    public void deleteNote(String id) {
        try {
            HttpEntity<?> request = new HttpEntity<>(createHeaders());
            restTemplate.exchange(NOTES_API + "/" + id, HttpMethod.DELETE, request, Void.class);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ApiException("La note avec l'ID spécifié n'existe pas.");
            }
            throw new ApiException("Erreur lors de la suppression de la note.", ex);
        } catch (Exception ex) {
            throw new ApiException("Une erreur inattendue s'est produite.", ex);
        }
    }

    public List<StudentAverageDTO> getStudentAverages(String semestre, String annee) {
        try {
            StringBuilder url = new StringBuilder(NOTES_API + "/averages");
            if (semestre != null || annee != null) {
                url.append("?");
                if (semestre != null) {
                    url.append("semestre=").append(semestre);
                    if (annee != null) {
                        url.append("&");
                    }
                }
                if (annee != null) {
                    url.append("annee=").append(annee);
                }
            }
            HttpEntity<?> request = new HttpEntity<>(createHeaders());
            ResponseEntity<StudentAverageDTO[]> response = restTemplate.exchange(
                url.toString(),
                HttpMethod.GET,
                request,
                StudentAverageDTO[].class
            );
            return Arrays.asList(response.getBody() != null ? response.getBody() : new StudentAverageDTO[0]);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                return new ArrayList<>();
            } else if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new ApiException("Non autorisé. Veuillez vous reconnecter.");
            } else {
                throw new ApiException("Erreur lors du chargement des moyennes.", ex);
            }
        } catch (Exception ex) {
            throw new ApiException("Une erreur inattendue s'est produite.", ex);
        }
    }

    public ClassStatisticsDTO getClassStatistics(String niveauId, String semestre, String annee, int topN) {
        try {
            StringBuilder url = new StringBuilder(NOTES_API + "/statistics/niveau/" + niveauId);
            url.append("?topN=").append(topN);
            if (semestre != null) {
                url.append("&semestre=").append(semestre);
            }
            if (annee != null) {
                url.append("&annee=").append(annee);
            }
            HttpEntity<?> request = new HttpEntity<>(createHeaders());
            ResponseEntity<ClassStatisticsDTO> response = restTemplate.exchange(
                url.toString(),
                HttpMethod.GET,
                request,
                ClassStatisticsDTO.class
            );
            return response.getBody();
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ApiException("Aucune statistique trouvée pour ce niveau.");
            } else if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new ApiException("Non autorisé. Veuillez vous reconnecter.");
            } else {
                throw new ApiException("Erreur lors du chargement des statistiques.", ex);
            }
        } catch (Exception ex) {
            throw new ApiException("Une erreur inattendue s'est produite.", ex);
        }
    }

    public List<ClassStatisticsDTO> getAllClassStatistics(String semestre, String annee) {
        try {
            List<Niveau> niveaux = getAllNiveaux();
            List<ClassStatisticsDTO> allStats = new ArrayList<>();
            for (Niveau niveau : niveaux) {
                try {
                    ClassStatisticsDTO stats = getClassStatistics(niveau.getId(), semestre, annee, 5);
                    if (stats != null) {
                        allStats.add(stats);
                    }
                } catch (ApiException ex) {
                    // Skip levels with no statistics
                    continue;
                }
            }
            return allStats;
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new ApiException("Non autorisé. Veuillez vous reconnecter.");
            }
            throw new ApiException("Erreur lors du chargement des statistiques.", ex);
        } catch (Exception ex) {
            throw new ApiException("Une erreur inattendue s'est produite.", ex);
        }
    }
}