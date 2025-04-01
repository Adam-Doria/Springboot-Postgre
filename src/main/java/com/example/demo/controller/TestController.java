package com.example.demo.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Contrôleur de test pour vérifier les autorisations
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {
    
    /**
     * Endpoint accessible à tous
     */
    @GetMapping("/all")
    public String allAccess() {
        return "Contenu Public";
    }
    
    /**
     * Endpoint accessible uniquement aux utilisateurs authentifiés
     */
    @GetMapping("/user")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public String userAccess() {
        return "Contenu Utilisateur";
    }
    
    /**
     * Endpoint accessible uniquement aux modérateurs
     */
    @GetMapping("/mod")
    @PreAuthorize("hasRole('MODERATOR')")
    public String moderatorAccess() {
        return "Contenu Modérateur";
    }
    
    /**
     * Endpoint accessible uniquement aux administrateurs
     */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "Contenu Administrateur";
    }
} 