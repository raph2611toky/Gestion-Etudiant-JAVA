package com.studentmanagement.service;

import com.studentmanagement.dto.EtudiantDTO;
import com.studentmanagement.model.Etudiant;
import com.studentmanagement.model.Responsable;
import com.studentmanagement.repository.EtudiantRepository;
import com.studentmanagement.repository.ResponsableRepository;
import com.studentmanagement.utils.FileStorageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EtudiantService {

    @Autowired
    private EtudiantRepository etudiantRepository;

    @Autowired
    private ResponsableRepository responsableRepository;

    @Autowired
    private FileStorageUtil fileStorageUtil;

    @Value("${server.base-url}")
    private String baseUrl;

    // Helper method to map Etudiant to EtudiantDTO
    private EtudiantDTO mapToDTO(Etudiant etudiant) {
        EtudiantDTO dto = new EtudiantDTO();
        dto.setId(etudiant.getId());
        dto.setMatricule(etudiant.getMatricule());
        dto.setPrenom(etudiant.getPrenom());
        dto.setNom(etudiant.getNom());
        dto.setEmail(etudiant.getEmail());
        dto.setAdresse(etudiant.getAdresse());
        dto.setNiveauClasse(etudiant.getNiveauClasse());
        dto.setPhoto(etudiant.getPhoto());
        dto.setPhoto_url(etudiant.getPhoto() != null ? baseUrl + etudiant.getPhoto() : null);
        dto.setResponsableId(etudiant.getResponsable().getId());
        return dto;
    }

    public EtudiantDTO addEtudiant(String matricule, String prenom, String nom, String email, String adresse,
                                   String niveauClasse, MultipartFile photo, String responsableId) throws IOException {
        if (etudiantRepository.findByMatricule(matricule).isPresent()) {
            throw new IllegalArgumentException("Matricule déjà utilisé.");
        }
        Responsable responsable = responsableRepository.findById(responsableId)
                .orElseThrow(() -> new IllegalArgumentException("Responsable non trouvé."));

        Etudiant etudiant = new Etudiant();
        etudiant.setMatricule(matricule);
        etudiant.setPrenom(prenom);
        etudiant.setNom(nom);
        etudiant.setEmail(email);
        etudiant.setAdresse(adresse);
        etudiant.setNiveauClasse(niveauClasse);
        if (photo != null && !photo.isEmpty()) {
            etudiant.setPhoto(fileStorageUtil.storeFile(photo));
        }
        etudiant.setResponsable(responsable);

        return mapToDTO(etudiantRepository.save(etudiant));
    }

    public List<EtudiantDTO> getAllEtudiantsByResponsable(String responsableId) {
        return etudiantRepository.findByResponsableId(responsableId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public Optional<EtudiantDTO> getEtudiantById(String id) {
        return etudiantRepository.findById(id)
                .map(this::mapToDTO);
    }

    public EtudiantDTO updateEtudiant(String id, String matricule, String prenom, String nom, String email,
                                     String adresse, String niveauClasse, MultipartFile photo, String responsableId) throws IOException {
        Optional<Etudiant> optionalEtudiant = etudiantRepository.findById(id);
        if (optionalEtudiant.isPresent()) {
            Etudiant etudiant = optionalEtudiant.get();
            if (!etudiant.getResponsable().getId().equals(responsableId)) {
                throw new IllegalArgumentException("Non autorisé à modifier cet étudiant.");
            }
            if (!etudiant.getMatricule().equals(matricule) &&
                etudiantRepository.findByMatricule(matricule).isPresent()) {
                throw new IllegalArgumentException("Matricule déjà utilisé.");
            }
            String oldPhoto = etudiant.getPhoto();
            etudiant.setMatricule(matricule);
            etudiant.setPrenom(prenom);
            etudiant.setNom(nom);
            etudiant.setEmail(email);
            etudiant.setAdresse(adresse);
            etudiant.setNiveauClasse(niveauClasse);
            if (photo != null && !photo.isEmpty()) {
                etudiant.setPhoto(fileStorageUtil.storeFile(photo));
                // Delete old photo if it exists
                if (oldPhoto != null) {
                    fileStorageUtil.deleteFile(oldPhoto);
                }
            }
            return mapToDTO(etudiantRepository.save(etudiant));
        }
        throw new IllegalArgumentException("Étudiant non trouvé.");
    }

    public EtudiantDTO updateNiveauClasse(String id, String niveauClasse, String responsableId) {
        Optional<Etudiant> optionalEtudiant = etudiantRepository.findById(id);
        if (optionalEtudiant.isPresent()) {
            Etudiant etudiant = optionalEtudiant.get();
            if (!etudiant.getResponsable().getId().equals(responsableId)) {
                throw new IllegalArgumentException("Non autorisé à modifier cet étudiant.");
            }
            etudiant.setNiveauClasse(niveauClasse);
            return mapToDTO(etudiantRepository.save(etudiant));
        }
        throw new IllegalArgumentException("Étudiant non trouvé.");
    }

    public EtudiantDTO updatePhoto(String id, MultipartFile photo, String responsableId) throws IOException {
        Optional<Etudiant> optionalEtudiant = etudiantRepository.findById(id);
        if (optionalEtudiant.isPresent()) {
            Etudiant etudiant = optionalEtudiant.get();
            if (!etudiant.getResponsable().getId().equals(responsableId)) {
                throw new IllegalArgumentException("Non autorisé à modifier cet étudiant.");
            }
            String oldPhoto = etudiant.getPhoto();
            if (photo != null && !photo.isEmpty()) {
                etudiant.setPhoto(fileStorageUtil.storeFile(photo));
                // Delete old photo if it exists
                if (oldPhoto != null) {
                    fileStorageUtil.deleteFile(oldPhoto);
                }
            }
            return mapToDTO(etudiantRepository.save(etudiant));
        }
        throw new IllegalArgumentException("Étudiant non trouvé.");
    }

    public EtudiantDTO updateAdresse(String id, String adresse, String responsableId) {
        Optional<Etudiant> optionalEtudiant = etudiantRepository.findById(id);
        if (optionalEtudiant.isPresent()) {
            Etudiant etudiant = optionalEtudiant.get();
            if (!etudiant.getResponsable().getId().equals(responsableId)) {
                throw new IllegalArgumentException("Non autorisé à modifier cet étudiant.");
            }
            etudiant.setAdresse(adresse);
            return mapToDTO(etudiantRepository.save(etudiant));
        }
        throw new IllegalArgumentException("Étudiant non trouvé.");
    }

    public void deleteEtudiant(String id, String responsableId) {
        Optional<Etudiant> optionalEtudiant = etudiantRepository.findById(id);
        if (optionalEtudiant.isPresent()) {
            Etudiant etudiant = optionalEtudiant.get();
            if (!etudiant.getResponsable().getId().equals(responsableId)) {
                throw new IllegalArgumentException("Non autorisé à supprimer cet étudiant.");
            }
            // Delete the photo file if it exists
            if (etudiant.getPhoto() != null) {
                try {
                    fileStorageUtil.deleteFile(etudiant.getPhoto());
                } catch (IOException e) {
                    System.err.println("Failed to delete photo: " + e.getMessage());
                }
            }
            etudiantRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Étudiant non trouvé.");
        }
    }
}