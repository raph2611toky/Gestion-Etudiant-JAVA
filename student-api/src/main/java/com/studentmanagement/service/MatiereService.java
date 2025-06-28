package com.studentmanagement.service;

import com.studentmanagement.dto.MatiereDTO;
import com.studentmanagement.model.Matiere;
import com.studentmanagement.model.Niveau;
import com.studentmanagement.repository.MatiereRepository;
import com.studentmanagement.repository.NiveauRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MatiereService {

    @Autowired
    private MatiereRepository matiereRepository;

    @Autowired
    private NiveauRepository niveauRepository;

    private MatiereDTO mapToDTO(Matiere matiere) {
        MatiereDTO dto = new MatiereDTO();
        dto.setId(matiere.getId());
        dto.setNom(matiere.getNom());
        dto.setCategorie(matiere.getCategorie());
        dto.setCoefficient(matiere.getCoefficient());
        dto.setNiveauId(matiere.getNiveau().getId());
        return dto;
    }

    public List<MatiereDTO> getAllMatieres() {
        return matiereRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public MatiereDTO addMatiere(String nom, String categorie, int coefficient, String niveauId) {
        Niveau niveau = niveauRepository.findById(niveauId)
                .orElseThrow(() -> new IllegalArgumentException("Niveau non trouvé."));
        if (matiereRepository.findByNomAndNiveauId(nom, niveauId).isPresent()) {
            throw new IllegalArgumentException("Matière déjà existante pour ce niveau.");
        }
        Matiere matiere = new Matiere();
        matiere.setNom(nom);
        matiere.setCategorie(categorie);
        matiere.setCoefficient(coefficient);
        matiere.setNiveau(niveau);
        return mapToDTO(matiereRepository.save(matiere));
    }

    public List<MatiereDTO> getAllMatieresByNiveau(String niveauId) {
        return matiereRepository.findByNiveauId(niveauId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public Optional<MatiereDTO> getMatiereById(String id) {
        return matiereRepository.findById(id)
                .map(this::mapToDTO);
    }

    public MatiereDTO updateMatiere(String id, String nom, String categorie, int coefficient, String niveauId) {
        Optional<Matiere> optionalMatiere = matiereRepository.findById(id);
        if (optionalMatiere.isPresent()) {
            Matiere matiere = optionalMatiere.get();
            Niveau niveau = niveauRepository.findById(niveauId)
                    .orElseThrow(() -> new IllegalArgumentException("Niveau non trouvé."));
            if (!matiere.getNom().equals(nom) || !matiere.getNiveau().getId().equals(niveauId)) {
                if (matiereRepository.findByNomAndNiveauId(nom, niveauId).isPresent()) {
                    throw new IllegalArgumentException("Matière déjà existante pour ce niveau.");
                }
            }
            matiere.setNom(nom);
            matiere.setCategorie(categorie);
            matiere.setCoefficient(coefficient);
            matiere.setNiveau(niveau);
            return mapToDTO(matiereRepository.save(matiere));
        }
        throw new IllegalArgumentException("Matière non trouvée.");
    }

    public void deleteMatiere(String id) {
        if (!matiereRepository.existsById(id)) {
            throw new IllegalArgumentException("Matière non trouvée.");
        }
        matiereRepository.deleteById(id);
    }
}