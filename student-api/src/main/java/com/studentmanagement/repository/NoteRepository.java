package com.studentmanagement.repository;

import com.studentmanagement.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, String> {
    List<Note> findByEtudiantId(String etudiantId);
    List<Note> findByMatiereId(String matiereId);
    List<Note> findBySemestre(String semestre);
    List<Note> findByAnnee(String annee);
    List<Note> findBySemestreAndAnnee(String semestre, String annee);
    List<Note> findByEtudiantIdAndSemestreAndAnnee(String etudiantId, String semestre, String annee);
    List<Note> findByEtudiantIdAndSemestre(String etudiantId, String semestre);
    List<Note> findByEtudiantIdAndAnnee(String etudiantId, String annee);
}