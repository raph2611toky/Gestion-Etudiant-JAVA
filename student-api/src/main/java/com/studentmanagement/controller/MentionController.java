package com.studentmanagement.controller;

import com.studentmanagement.dto.MentionDTO;
import com.studentmanagement.dto.MentionRequestDTO;
import com.studentmanagement.service.MentionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mentions")
public class MentionController {

    @Autowired
    private MentionService service;

    @PostMapping
    public ResponseEntity<MentionDTO> addMention(@RequestBody MentionRequestDTO request) {
        return ResponseEntity.ok(service.addMention(request.getName(), request.getDescription()));
    }

    @GetMapping
    public ResponseEntity<List<MentionDTO>> getAllMentions() {
        return ResponseEntity.ok(service.getAllMentions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MentionDTO> getMentionById(@PathVariable String id) {
        return service.getMentionById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<MentionDTO> updateMention(@PathVariable String id, @RequestBody MentionRequestDTO request) {
        return ResponseEntity.ok(service.updateMention(id, request.getName(), request.getDescription()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMention(@PathVariable String id) {
        service.deleteMention(id);
        return ResponseEntity.noContent().build();
    }
}