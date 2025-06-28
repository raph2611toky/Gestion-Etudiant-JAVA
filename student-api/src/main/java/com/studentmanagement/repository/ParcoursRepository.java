package com.studentmanagement.repository;

import com.studentmanagement.model.Parcours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParcoursRepository extends JpaRepository<Parcours, String> {
    Optional<Parcours> findByNomAndMentionId(String nom, String mentionId);

    List<Parcours> findByMentionId(String mentionId);
}