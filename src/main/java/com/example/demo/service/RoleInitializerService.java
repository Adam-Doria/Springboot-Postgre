package com.example.demo.service;

import com.example.demo.model.ERole;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Service pour initialiser les rôles et un utilisateur de test
 */
@Service
public class RoleInitializerService {

    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * Initialise les rôles et un utilisateur admin au démarrage de l'application
     */
    @PostConstruct
    public void init() {
        initRoles();
        createAdminUserIfNotExists();
    }
    
    /**
     * Initialise les rôles ROLE_USER, ROLE_MODERATOR et ROLE_ADMIN
     */
    private void initRoles() {
        if (roleRepository.count() == 0) {
            // Créer les rôles s'ils n'existent pas
            for (ERole eRole : ERole.values()) {
                Role role = new Role(eRole);
                roleRepository.save(role);
            }
            System.out.println("Rôles initialisés dans la base de données");
        }
    }
    
    /**
     * Crée un utilisateur admin de test si aucun utilisateur n'existe
     */
    private void createAdminUserIfNotExists() {
        if (userRepository.count() == 0) {
            // Créer un utilisateur admin de test
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("admin123")); // Mot de passe encodé
            admin.setDisplayName("Administrateur");
            admin.setIsOnline(false);
            admin.setCreatedAt(LocalDateTime.now());
            admin.setUpdatedAt(LocalDateTime.now());
            
            // Attribuer le rôle admin
            Set<Role> roles = new HashSet<>();
            Optional<Role> adminRole = roleRepository.findByName(ERole.ROLE_ADMIN);
            adminRole.ifPresent(roles::add);
            admin.setRoles(roles);
            
            userRepository.save(admin);
            System.out.println("Utilisateur admin créé avec succès : admin / admin123");
        }
    }
} 