package com.studentmanagement.controller;

import com.studentmanagement.dto.NoteDTO;
import com.studentmanagement.dto.NoteRequestDTO;
import com.studentmanagement.dto.StudentAverageDTO;
import com.studentmanagement.dto.ClassStatisticsDTO;
import com.studentmanagement.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    @Autowired
    private NoteService service;

    @PostMapping
    public ResponseEntity<NoteDTO> addNote(@RequestBody NoteRequestDTO request) {
        return ResponseEntity.ok(service.addNote(request.getEtudiantId(), request.getMatiereId(),
                request.getValeur(), request.getSemestre(), request.getAnnee()));
    }

    @GetMapping
    public ResponseEntity<List<NoteDTO>> getAllNotes(
            @RequestParam(required = false) String semestre,
            @RequestParam(required = false) String annee) {
        return ResponseEntity.ok(service.getAllNotes(semestre, annee));
    }

    @GetMapping("/etudiant/{etudiantId}")
    public ResponseEntity<List<NoteDTO>> getNotesByEtudiant(@PathVariable String etudiantId) {
        return ResponseEntity.ok(service.getNotesByEtudiant(etudiantId));
    }

    @GetMapping("/matiere/{matiereId}")
    public ResponseEntity<List<NoteDTO>> getNotesByMatiere(@PathVariable String matiereId) {
        return ResponseEntity.ok(service.getNotesByMatiere(matiereId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteDTO> getNoteById(@PathVariable String id) {
        return service.getNoteById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteDTO> updateNote(@PathVariable String id, @RequestBody NoteRequestDTO request) {
        return ResponseEntity.ok(service.updateNote(id, request.getEtudiantId(), request.getMatiereId(),
                request.getValeur(), request.getSemestre(), request.getAnnee()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable String id) {
        service.deleteNote(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/averages")
    public ResponseEntity<List<StudentAverageDTO>> getStudentAverages(
            @RequestParam(required = false) String semestre,
            @RequestParam(required = false) String annee) {
        return ResponseEntity.ok(service.getStudentAverages(semestre, annee));
    }

    @GetMapping("/statistics/niveau/{niveauId}")
    public ResponseEntity<ClassStatisticsDTO> getClassStatistics(
            @PathVariable String niveauId,
            @RequestParam(required = false) String semestre,
            @RequestParam(required = false) String annee,
            @RequestParam(defaultValue = "5") int topN) {
        return ResponseEntity.ok(service.getClassStatistics(niveauId, semestre, annee, topN));
    }
}