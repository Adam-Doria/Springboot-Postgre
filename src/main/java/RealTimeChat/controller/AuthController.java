package RealTimeChat.controller;

import RealTimeChat.model.User;
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
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentification", description = "API d'authentification des utilisateurs")
public class AuthController {

    @Autowired
    private UserService userService;

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

            // Retourner l'utilisateur sans exposer le mot de passe hashé
            registeredUser.setPassword("[PROTECTED]");

            return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
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

        return userService.findUserByUsername(username)
                .filter(user -> userService.verifyPassword(user, password))
                .map(user -> {
                    userService.updateUserOnlineStatus(user.getId(), true);

                    Map<String, Object> response = new HashMap<>();
                    response.put("message", "Connexion réussie");

                    user.setPassword("[PROTECTED]");
                    response.put("user", user);

                    return new ResponseEntity<>(response, HttpStatus.OK);
                })
                .orElseGet(() -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("error", "Nom d'utilisateur ou mot de passe incorrect");
                    return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
                });
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