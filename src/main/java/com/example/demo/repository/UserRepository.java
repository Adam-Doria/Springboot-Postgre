package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour les opérations CRUD sur l'entité User
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    /**
     * Trouve un utilisateur par son nom d'utilisateur
     * @param username le nom d'utilisateur
     * @return l'utilisateur s'il existe
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Vérifie si un nom d'utilisateur existe déjà
     * @param username le nom d'utilisateur à vérifier
     * @return true si le nom d'utilisateur existe déjà
     */
    Boolean existsByUsername(String username);
    
    /**
     * Vérifie si un email existe déjà
     * @param email l'email à vérifier
     * @return true si l'email existe déjà
     */
    Boolean existsByEmail(String email);
    
    /**
     * Trouve tous les utilisateurs en ligne
     * @return une liste d'utilisateurs en ligne
     */
    List<User> findByIsOnlineTrue();
} 