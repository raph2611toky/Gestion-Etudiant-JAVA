package com.studentmanagement.controller;

import com.studentmanagement.model.Responsable;
import com.studentmanagement.model.ResponsableResponse;
import com.studentmanagement.security.JwtUtil;
import com.studentmanagement.service.ResponsableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/responsables")
public class ResponsableController {

    @Autowired
    private ResponsableService responsableService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<Responsable> registerResponsable(@RequestBody Responsable responsable) {
        Responsable newResponsable = responsableService.registerResponsable(responsable);
        return ResponseEntity.ok(newResponsable);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginResponsable(@RequestBody LoginRequest loginRequest) {
        String token = responsableService.loginResponsable(loginRequest.getEmail(), loginRequest.getMotDePasse());
        LoginResponse response = new LoginResponse(token, "Bearer", 24 * 60 * 60); // 24 hours in seconds
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<ResponsableResponse> getProfile() {
        String responsableId = SecurityContextHolder.getContext().getAuthentication().getName();

        Responsable responsable = responsableService.getResponsableProfile(responsableId);

        String token = jwtUtil.generateToken(responsable.getId(), responsable.getEmail());

        ResponsableResponse response = new ResponsableResponse();
        response.setId(responsable.getId());
        response.setPrenom(responsable.getPrenom());
        response.setNom(responsable.getNom());
        response.setEmail(responsable.getEmail());
        response.setToken(token);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<Responsable>> getAllResponsables() {
        List<Responsable> responsables = responsableService.getAllResponsables();
        return ResponseEntity.ok(responsables);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Responsable> getResponsableById(@PathVariable String id) {
        Optional<Responsable> responsable = responsableService.getResponsableById(id);
        return responsable.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Responsable> updateResponsable(@PathVariable String id,
            @RequestBody Responsable responsableDetails) {
        String responsableId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!id.equals(responsableId)) {
            return ResponseEntity.status(403).build();
        }
        Responsable updatedResponsable = responsableService.updateResponsable(id, responsableDetails);
        return ResponseEntity.ok(updatedResponsable);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResponsable(@PathVariable String id) {
        String responsableId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!id.equals(responsableId)) {
            return ResponseEntity.status(403).build();
        }
        responsableService.deleteResponsable(id);
        return ResponseEntity.noContent().build();
    }
}

class LoginRequest {
    private String email;
    private String motDePasse;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }
}

class LoginResponse {
    private String token;
    private String tokenType;
    private long expiresIn;

    public LoginResponse(String token, String tokenType, long expiresIn) {
        this.token = token;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }
}

// package com.studentmanagement.controller;

// import com.studentmanagement.dto.ResponsableRequestDTO;
// import com.studentmanagement.model.Responsable;
// import com.studentmanagement.service.ResponsableService;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.web.bind.annotation.*;

// import java.util.List;
// import java.util.Optional;

// @RestController
// @RequestMapping("/api/responsables")
// public class ResponsableController {

// @Autowired
// private ResponsableService responsableService;

// @PostMapping("/register")
// public ResponseEntity<Responsable> registerResponsable(@RequestBody
// ResponsableRequestDTO request) {
// Responsable responsable = new Responsable();
// responsable.setPrenom(request.getPrenom());
// responsable.setNom(request.getNom());
// responsable.setEmail(request.getEmail());
// responsable.setMotDePasse(request.getMotDePasse());
// Responsable newResponsable =
// responsableService.registerResponsable(responsable);
// return ResponseEntity.ok(newResponsable);
// }

// @PostMapping("/login")
// public ResponseEntity<LoginResponse> loginResponsable(@RequestBody
// LoginRequest loginRequest) {
// String token = responsableService.loginResponsable(loginRequest.getEmail(),
// loginRequest.getMotDePasse());
// LoginResponse response = new LoginResponse(token, "Bearer", 24 * 60 * 60);
// return ResponseEntity.ok(response);
// }

// @GetMapping("/me")
// public ResponseEntity<Responsable> getProfile() {
// String responsableId =
// SecurityContextHolder.getContext().getAuthentication().getName();
// Responsable responsable =
// responsableService.getResponsableProfile(responsableId);
// return ResponseEntity.ok(responsable);
// }

// @GetMapping
// public ResponseEntity<List<Responsable>> getAllResponsables() {
// List<Responsable> responsables = responsableService.getAllResponsables();
// return ResponseEntity.ok(responsables);
// }

// @GetMapping("/{id}")
// public ResponseEntity<Responsable> getResponsableById(@PathVariable String
// id) {
// Optional<Responsable> responsable =
// responsableService.getResponsableById(id);
// return responsable.map(ResponseEntity::ok)
// .orElseGet(() -> ResponseEntity.notFound().build());
// }

// @PutMapping("/{id}")
// public ResponseEntity<Responsable> updateResponsable(@PathVariable String id,
// @RequestBody ResponsableRequestDTO request) {
// String responsableId =
// SecurityContextHolder.getContext().getAuthentication().getName();
// if (!id.equals(responsableId)) {
// return ResponseEntity.status(403).build();
// }
// Responsable responsable = new Responsable();
// responsable.setPrenom(request.getPrenom());
// responsable.setNom(request.getNom());
// responsable.setEmail(request.getEmail());
// responsable.setMotDePasse(request.getMotDePasse());
// Responsable updatedResponsable = responsableService.updateResponsable(id,
// responsable);
// return ResponseEntity.ok(updatedResponsable);
// }

// @DeleteMapping("/{id}")
// public ResponseEntity<Void> deleteResponsable(@PathVariable String id) {
// String responsableId =
// SecurityContextHolder.getContext().getAuthentication().getName();
// if (!id.equals(responsableId)) {
// return ResponseEntity.status(403).build();
// }
// responsableService.deleteResponsable(id);
// return ResponseEntity.noContent().build();
// }
// }

// class LoginRequest {
// private String email;
// private String motDePasse;

// public String getEmail() {
// return email;
// }

// public void setEmail(String email) {
// this.email = email;
// }

// public String getMotDePasse() {
// return motDePasse;
// }

// public void setMotDePasse(String motDePasse) {
// this.motDePasse = motDePasse;
// }
// }

// class LoginResponse {
// private String token;
// private String tokenType;
// private long expiresIn;

// public LoginResponse(String token, String tokenType, long expiresIn) {
// this.token = token;
// this.tokenType = tokenType;
// this.expiresIn = expiresIn;
// }

// public String getToken() {
// return token;
// }

// public void setToken(String token) {
// this.token = token;
// }

// public String getTokenType() {
// return tokenType;
// }

// public void setTokenType(String tokenType) {
// this.tokenType = tokenType;
// }

// public long getExpiresIn() {
// return expiresIn;
// }

// public void setExpiresIn(long expiresIn) {
// this.expiresIn = expiresIn;
// }
// }