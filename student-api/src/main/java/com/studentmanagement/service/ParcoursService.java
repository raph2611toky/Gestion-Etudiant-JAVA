package com.studentmanagement.service;

import com.studentmanagement.dto.ParcoursDTO;
import com.studentmanagement.model.Mention;
import com.studentmanagement.model.Parcours;
import com.studentmanagement.repository.MentionRepository;
import com.studentmanagement.repository.ParcoursRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ParcoursService {

    @Autowired
    private ParcoursRepository parcoursRepository;

    @Autowired
    private MentionRepository mentionRepository;

    private ParcoursDTO mapToDTO(Parcours parcours) {
        ParcoursDTO dto = new ParcoursDTO();
        dto.setId(parcours.getId());
        dto.setNom(parcours.getNom());
        dto.setMentionId(parcours.getMention().getId());
        dto.setDescription(parcours.getDescription());
        return dto;
    }

    public List<ParcoursDTO> getAllParcours() {
        return parcoursRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ParcoursDTO addParcours(String nom, String mentionId, String description) {
        Mention mention = mentionRepository.findById(mentionId)
                .orElseThrow(() -> new IllegalArgumentException("Mention non trouvée."));
        if (parcoursRepository.findByNomAndMentionId(nom, mentionId).isPresent()) {
            throw new IllegalArgumentException("Parcours déjà existant pour cette mention.");
        }
        Parcours parcours = new Parcours();
        parcours.setNom(nom);
        parcours.setMention(mention);
        parcours.setDescription(description);
        return mapToDTO(parcoursRepository.save(parcours));
    }

    public List<ParcoursDTO> getAllParcoursByMention(String mentionId) {
        return parcoursRepository.findByMentionId(mentionId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public Optional<ParcoursDTO> getParcoursById(String id) {
        return parcoursRepository.findById(id)
                .map(this::mapToDTO);
    }

    public ParcoursDTO updateParcours(String id, String nom, String mentionId, String description) {
        Optional<Parcours> optionalParcours = parcoursRepository.findById(id);
        if (optionalParcours.isPresent()) {
            Parcours parcours = optionalParcours.get();
            Mention mention = mentionRepository.findById(mentionId)
                    .orElseThrow(() -> new IllegalArgumentException("Mention non trouvée."));
            if (!parcours.getNom().equals(nom) || !parcours.getMention().getId().equals(mentionId)) {
                if (parcoursRepository.findByNomAndMentionId(nom, mentionId).isPresent()) {
                    throw new IllegalArgumentException("Parcours déjà existant pour cette mention.");
                }
            }
            parcours.setNom(nom);
            parcours.setMention(mention);
            parcours.setDescription(description);
            return mapToDTO(parcoursRepository.save(parcours));
        }
        throw new IllegalArgumentException("Parcours non trouvé.");
    }

    public void deleteParcours(String id) {
        if (!parcoursRepository.existsById(id)) {
            throw new IllegalArgumentException("Parcours non trouvé.");
        }
        parcoursRepository.deleteById(id);
    }
}