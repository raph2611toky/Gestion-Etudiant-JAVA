package com.studentmanagement.repository;

import com.studentmanagement.model.Matiere;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatiereRepository extends JpaRepository<Matiere, String> {
    Optional<Matiere> findByNomAndNiveauId(String nom, String niveauId);
    List<Matiere> findByNiveauId(String niveauId);
}