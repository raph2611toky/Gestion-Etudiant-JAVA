package com.studentmanagement.controller;

import com.studentmanagement.dto.EtudiantDTO;
import com.studentmanagement.service.EtudiantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/etudiants")
public class EtudiantController {

    @Autowired
    private EtudiantService etudiantService;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<EtudiantDTO> addEtudiant(
            @RequestParam("matricule") String matricule,
            @RequestParam("prenom") String prenom,
            @RequestParam("nom") String nom,
            @RequestParam("email") String email,
            @RequestParam(value = "adresse", required = false) String adresse,
            @RequestParam(value = "niveauClasse", required = false) String niveauClasse,
            @RequestParam(value = "photo", required = false) MultipartFile photo) throws IOException {
        String responsableId = SecurityContextHolder.getContext().getAuthentication().getName();
        EtudiantDTO newEtudiant = etudiantService.addEtudiant(matricule, prenom, nom, email, adresse, niveauClasse, photo, responsableId);
        return ResponseEntity.ok(newEtudiant);
    }

    @GetMapping
    public ResponseEntity<List<EtudiantDTO>> getAllEtudiants() {
        String responsableId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<EtudiantDTO> etudiants = etudiantService.getAllEtudiantsByResponsable(responsableId);
        return ResponseEntity.ok(etudiants);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EtudiantDTO> getEtudiantById(@PathVariable String id) {
        Optional<EtudiantDTO> etudiant = etudiantService.getEtudiantById(id);
        if (etudiant.isPresent()) {
            String responsableId = SecurityContextHolder.getContext().getAuthentication().getName();
            if (!etudiant.get().getResponsableId().equals(responsableId)) {
                return ResponseEntity.status(403).build();
            }
            return ResponseEntity.ok(etudiant.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping(path = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<EtudiantDTO> updateEtudiant(
            @PathVariable String id,
            @RequestParam("matricule") String matricule,
            @RequestParam("prenom") String prenom,
            @RequestParam("nom") String nom,
            @RequestParam("email") String email,
            @RequestParam(value = "adresse", required = false) String adresse,
            @RequestParam(value = "niveauClasse", required = false) String niveauClasse,
            @RequestParam(value = "photo", required = false) MultipartFile photo) throws IOException {
        String responsableId = SecurityContextHolder.getContext().getAuthentication().getName();
        EtudiantDTO updatedEtudiant = etudiantService.updateEtudiant(id, matricule, prenom, nom, email, adresse, niveauClasse, photo, responsableId);
        return ResponseEntity.ok(updatedEtudiant);
    }

    @PutMapping("/{id}/niveau")
    public ResponseEntity<EtudiantDTO> updateNiveauClasse(@PathVariable String id, @RequestBody String niveauClasse) {
        String responsableId = SecurityContextHolder.getContext().getAuthentication().getName();
        EtudiantDTO updatedEtudiant = etudiantService.updateNiveauClasse(id, niveauClasse, responsableId);
        return ResponseEntity.ok(updatedEtudiant);
    }

    @PutMapping(path = "/{id}/photo", consumes = "multipart/form-data")
    public ResponseEntity<EtudiantDTO> updatePhoto(@PathVariable String id, @RequestParam("photo") MultipartFile photo) throws IOException {
        String responsableId = SecurityContextHolder.getContext().getAuthentication().getName();
        EtudiantDTO updatedEtudiant = etudiantService.updatePhoto(id, photo, responsableId);
        return ResponseEntity.ok(updatedEtudiant);
    }

    @PutMapping("/{id}/adresse")
    public ResponseEntity<EtudiantDTO> updateAdresse(@PathVariable String id, @RequestBody String adresse) {
        String responsableId = SecurityContextHolder.getContext().getAuthentication().getName();
        EtudiantDTO updatedEtudiant = etudiantService.updateAdresse(id, adresse, responsableId);
        return ResponseEntity.ok(updatedEtudiant);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEtudiant(@PathVariable String id) {
        String responsableId = SecurityContextHolder.getContext().getAuthentication().getName();
        etudiantService.deleteEtudiant(id, responsableId);
        return ResponseEntity.noContent().build();
    }
}