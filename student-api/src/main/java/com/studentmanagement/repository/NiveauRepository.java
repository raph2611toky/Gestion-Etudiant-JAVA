package com.studentmanagement.repository;

import com.studentmanagement.model.Niveau;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NiveauRepository extends JpaRepository<Niveau, String> {
    Optional<Niveau> findByNom(String nom);

    Optional<Niveau> findById(String id);
}