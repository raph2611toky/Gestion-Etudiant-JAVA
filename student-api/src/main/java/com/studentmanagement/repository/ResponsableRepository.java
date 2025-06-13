package com.studentmanagement.repository;

import com.studentmanagement.model.Responsable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResponsableRepository extends JpaRepository<Responsable, String> {
    Optional<Responsable> findByEmail(String email);
}