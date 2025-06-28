package com.studentmanagement.service;

import com.studentmanagement.dto.MentionDTO;
import com.studentmanagement.model.Mention;
import com.studentmanagement.repository.MentionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MentionService {

    @Autowired
    private MentionRepository mentionRepository;

    private MentionDTO mapToDTO(Mention mention) {
        MentionDTO dto = new MentionDTO();
        dto.setId(mention.getId());
        dto.setNom(mention.getNom());
        dto.setDescription(mention.getDescription());
        return dto;
    }

    public MentionDTO addMention(String nom, String description) {
        if (mentionRepository.findByNom(nom).isPresent()) {
            throw new IllegalArgumentException("Mention déjà existante.");
        }
        Mention mention = new Mention();
        mention.setNom(nom);
        mention.setDescription(description);
        return mapToDTO(mentionRepository.save(mention));
    }

    public List<MentionDTO> getAllMentions() {
        return mentionRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public Optional<MentionDTO> getMentionById(String id) {
        return mentionRepository.findById(id)
                .map(this::mapToDTO);
    }

    public MentionDTO updateMention(String id, String nom, String description) {
        Optional<Mention> optionalMention = mentionRepository.findById(id);
        if (optionalMention.isPresent()) {
            Mention mention = optionalMention.get();
            if (!mention.getNom().equals(nom) && mentionRepository.findByNom(nom).isPresent()) {
                throw new IllegalArgumentException("Mention déjà existante.");
            }
            mention.setNom(nom);
            mention.setDescription(description);
            return mapToDTO(mentionRepository.save(mention));
        }
        throw new IllegalArgumentException("Mention non trouvée.");
    }

    public void deleteMention(String id) {
        if (!mentionRepository.existsById(id)) {
            throw new IllegalArgumentException("Mention non trouvée.");
        }
        mentionRepository.deleteById(id);
    }
}