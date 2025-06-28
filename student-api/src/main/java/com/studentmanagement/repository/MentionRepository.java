package com.studentmanagement.repository;

import com.studentmanagement.model.Mention;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MentionRepository extends JpaRepository<Mention, String> {
    Optional<Mention> findByNom(String nom);
}