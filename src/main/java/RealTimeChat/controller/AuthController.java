package RealTimeChat.controller;

import RealTimeChat.dto.JwtResponse;
import RealTimeChat.model.User;
import RealTimeChat.security.JwtUtils;
import RealTimeChat.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentification", description = "API d'authentification des utilisateurs")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Operation(summary = "Enregistrer un nouvel utilisateur",
            description = "Enregistre un nouvel utilisateur avec un mot de passe hashé")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Utilisateur créé avec succès",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides ou utilisateur déjà existant")
    })
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            User registeredUser = userService.registerUser(user);

            // Générer un token JWT pour le nouvel utilisateur, en gros ca lui prmet de se connecter direct quand
            // il n'a pas besoin de se ré-authentifier
            String token = jwtUtils.generateToken(registeredUser.getId());

            JwtResponse response = new JwtResponse(
                    token,
                    registeredUser.getId(),
                    registeredUser.getUsername(),
                    registeredUser.getDisplayName(),
                    registeredUser.getIsOnline()
            );

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Connecter un utilisateur",
            description = "Authentifie un utilisateur avec son nom d'utilisateur et mot de passe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentification réussie"),
            @ApiResponse(responseCode = "401", description = "Authentification échouée")
    })
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        if (username == null || password == null) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Nom d'utilisateur et mot de passe requis");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));

            // Si l'authentification réussit, générer un token JWT
            User user = userService.findUserByUsername(username).orElseThrow();
            userService.updateUserOnlineStatus(user.getId(), true);

            String token = jwtUtils.generateToken(user.getId());

            // Créer la réponse avec le token et les infos utilisateur
            JwtResponse response = new JwtResponse(
                    token,
                    user.getId(),
                    user.getUsername(),
                    user.getDisplayName(),
                    true
            );

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (DisabledException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Compte utilisateur désactivé");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        } catch (BadCredentialsException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Nom d'utilisateur ou mot de passe incorrect");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    @Operation(summary = "Déconnecter un utilisateur",
            description = "Met à jour le statut en ligne d'un utilisateur lors de la déconnexion")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Déconnexion réussie"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@RequestBody Map<String, Integer> requestBody) {
        Integer userId = requestBody.get("userId");

        if (userId == null) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "ID utilisateur requis");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            userService.updateUserOnlineStatus(userId, false);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Déconnexion réussie");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}