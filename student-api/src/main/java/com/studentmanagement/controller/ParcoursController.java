package com.studentmanagement.controller;

import com.studentmanagement.dto.ParcoursDTO;
import com.studentmanagement.dto.ParcoursRequestDTO;
import com.studentmanagement.service.ParcoursService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parcours")
public class ParcoursController {

    @Autowired
    private ParcoursService service;

    @GetMapping
    public ResponseEntity<List<ParcoursDTO>> getAllParcours() {
        return ResponseEntity.ok(service.getAllParcours());
    }

    @PostMapping
    public ResponseEntity<ParcoursDTO> addParcours(@RequestBody ParcoursRequestDTO request) {
        return ResponseEntity
                .ok(service.addParcours(request.getName(), request.getMentionId(), request.getDescription()));
    }

    @GetMapping("/mention/{mentionId}")
    public ResponseEntity<List<ParcoursDTO>> getAllParcoursByMention(@PathVariable String mentionId) {
        return ResponseEntity.ok(service.getAllParcoursByMention(mentionId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParcoursDTO> getParcoursById(@PathVariable String id) {
        return service.getParcoursById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParcoursDTO> updateParcours(@PathVariable String id,
            @RequestBody ParcoursRequestDTO request) {
        return ResponseEntity
                .ok(service.updateParcours(id, request.getName(), request.getMentionId(), request.getDescription()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParcours(@PathVariable String id) {
        service.deleteParcours(id);
        return ResponseEntity.noContent().build();
    }
}