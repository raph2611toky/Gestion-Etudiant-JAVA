package com.studentmanagement.controller;

import com.studentmanagement.dto.NiveauDTO;
import com.studentmanagement.dto.NiveauRequestDTO;
import com.studentmanagement.service.NiveauService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/niveaux")
public class NiveauController {

    @Autowired
    private NiveauService service;

    @PostMapping
    public ResponseEntity<NiveauDTO> addNiveau(@RequestBody NiveauRequestDTO request) {
        return ResponseEntity.ok(service.addNiveau(request.getName(), request.getDescription()));
    }

    @GetMapping
    public ResponseEntity<List<NiveauDTO>> getAllNiveaux() {
        return ResponseEntity.ok(service.getAllNiveaux());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NiveauDTO> getNiveauById(@PathVariable String id) {
        return service.getNiveauById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<NiveauDTO> updateNiveau(@PathVariable String id, @RequestBody NiveauRequestDTO request) {
        return ResponseEntity.ok(service.updateNiveau(id, request.getName(), request.getDescription()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNiveau(@PathVariable String id) {
        service.deleteNiveau(id);
        return ResponseEntity.noContent().build();
    }
}