package com.studentmanagement.controller;

import com.studentmanagement.dto.MatiereDTO;
import com.studentmanagement.dto.MatiereRequestDTO;
import com.studentmanagement.service.MatiereService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matieres")
public class MatiereController {

    @Autowired
    private MatiereService service;

    @PostMapping
    public ResponseEntity<MatiereDTO> addMatiere(@RequestBody MatiereRequestDTO request) {
        return ResponseEntity.ok(service.addMatiere(request.getNom(), request.getCategorie(), request.getCoefficient(), request.getNiveauId()));
    }

    @GetMapping
    public ResponseEntity<List<MatiereDTO>> getAllMatieres() {
        return ResponseEntity.ok(service.getAllMatieres());
    }

    @GetMapping("/niveau/{niveauId}")
    public ResponseEntity<List<MatiereDTO>> getAllMatieresByNiveau(@PathVariable String niveauId) {
        return ResponseEntity.ok(service.getAllMatieresByNiveau(niveauId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MatiereDTO> getMatiereById(@PathVariable String id) {
        return service.getMatiereById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<MatiereDTO> updateMatiere(@PathVariable String id, @RequestBody MatiereRequestDTO request) {
        return ResponseEntity.ok(service.updateMatiere(id, request.getNom(), request.getCategorie(), request.getCoefficient(), request.getNiveauId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMatiere(@PathVariable String id) {
        service.deleteMatiere(id);
        return ResponseEntity.noContent().build();
    }
}