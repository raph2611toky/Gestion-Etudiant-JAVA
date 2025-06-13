package com.studentmanagement.service;

import com.studentmanagement.model.Responsable;
import com.studentmanagement.repository.ResponsableRepository;
import com.studentmanagement.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ResponsableService {

    @Autowired
    private ResponsableRepository responsableRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public Responsable registerResponsable(Responsable responsable) {
        if (responsableRepository.findByEmail(responsable.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Cet email est déjà utilisé.");
        }
        return responsableRepository.save(responsable);
    }

    public String loginResponsable(String email, String motDePasse) {
        Optional<Responsable> optionalResponsable = responsableRepository.findByEmail(email);
        if (optionalResponsable.isPresent()) {
            Responsable responsable = optionalResponsable.get();
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            if (encoder.matches(motDePasse, responsable.getMotDePasse())) {
                return jwtUtil.generateToken(responsable.getId(), responsable.getEmail());
            }
        }
        throw new IllegalArgumentException("Email ou mot de passe incorrect.");
    }

    public Responsable getResponsableProfile(String id) {
        return responsableRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Responsable non trouvé."));
    }

    public List<Responsable> getAllResponsables() {
        return responsableRepository.findAll();
    }

    public Optional<Responsable> getResponsableById(String id) {
        return responsableRepository.findById(id);
    }

    public Responsable updateResponsable(String id, Responsable responsableDetails) {
        Optional<Responsable> optionalResponsable = responsableRepository.findById(id);
        if (optionalResponsable.isPresent()) {
            Responsable responsable = optionalResponsable.get();
            responsable.setPrenom(responsableDetails.getPrenom());
            responsable.setNom(responsableDetails.getNom());
            responsable.setEmail(responsableDetails.getEmail());
            if (responsableDetails.getMotDePasse() != null && !responsableDetails.getMotDePasse().isEmpty()) {
                responsable.setMotDePasse(responsableDetails.getMotDePasse());
            }
            return responsableRepository.save(responsable);
        }
        throw new IllegalArgumentException("Responsable non trouvé.");
    }

    public void deleteResponsable(String id) {
        if (!responsableRepository.existsById(id)) {
            throw new IllegalArgumentException("Responsable non trouvé.");
        }
        responsableRepository.deleteById(id);
    }
}