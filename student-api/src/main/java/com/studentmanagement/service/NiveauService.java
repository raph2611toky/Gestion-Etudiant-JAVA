package com.studentmanagement.service;

import com.studentmanagement.dto.NiveauDTO;
import com.studentmanagement.model.Niveau;
import com.studentmanagement.repository.NiveauRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NiveauService {

    @Autowired
    private NiveauRepository niveauRepository;

    private NiveauDTO mapToDTO(Niveau niveau) {
        NiveauDTO dto = new NiveauDTO();
        dto.setId(niveau.getId());
        dto.setNom(niveau.getNom());
        dto.setDescription(niveau.getDescription());
        return dto;
    }

    public NiveauDTO addNiveau(String nom, String description) {
        if (niveauRepository.findByNom(nom).isPresent()) {
            throw new IllegalArgumentException("Niveau déjà existant.");
        }
        Niveau niveau = new Niveau();
        niveau.setNom(nom);
        niveau.setDescription(description);
        return mapToDTO(niveauRepository.save(niveau));
    }

    public List<NiveauDTO> getAllNiveaux() {
        return niveauRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public Optional<NiveauDTO> getNiveauById(String id) {
        return niveauRepository.findById(id)
                .map(this::mapToDTO);
    }

    public NiveauDTO updateNiveau(String id, String nom, String description) {
        Optional<Niveau> optionalNiveau = niveauRepository.findById(id);
        if (optionalNiveau.isPresent()) {
            Niveau niveau = optionalNiveau.get();
            if (!niveau.getNom().equals(nom) && niveauRepository.findByNom(nom).isPresent()) {
                throw new IllegalArgumentException("Niveau déjà existant.");
            }
            niveau.setNom(nom);
            niveau.setDescription(description);
            return mapToDTO(niveauRepository.save(niveau));
        }
        throw new IllegalArgumentException("Niveau non trouvé.");
    }

    public void deleteNiveau(String id) {
        if (!niveauRepository.existsById(id)) {
            throw new IllegalArgumentException("Niveau non trouvé.");
        }
        niveauRepository.deleteById(id);
    }
}