package com.studentmanagement.repository;

import com.studentmanagement.model.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EtudiantRepository extends JpaRepository<Etudiant, String> {
    Optional<Etudiant> findByMatricule(String matricule);
    List<Etudiant> findByResponsableId(String responsableId);
    List<Etudiant> findByNiveauId(String niveauId);
}