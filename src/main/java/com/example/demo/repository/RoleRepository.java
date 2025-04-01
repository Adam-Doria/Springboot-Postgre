package com.example.demo.repository;

import com.example.demo.model.ERole;
import com.example.demo.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository pour les opérations CRUD sur l'entité Role
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    /**
     * Trouve un rôle par son nom
     * @param name le nom du rôle
     * @return le rôle s'il existe
     */
    Optional<Role> findByName(ERole name);
} 