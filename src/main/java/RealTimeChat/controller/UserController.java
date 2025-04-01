package RealTimeChat.controller;

import RealTimeChat.model.User;
import RealTimeChat.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Utilisateurs", description = "API de gestion des utilisateurs")
public class UserController {

    @Autowired
    private UserService userService ;

//@TODO move this route inside authController once it has been created
    @PostMapping
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        try {
            User registeredUser = userService.registerUser(user);
            return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Récupérer un utilisateur par son nom d'utilisateur",
            description = "Renvoie les détails d'un utilisateur basé sur son nom d'utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilisateur trouvé",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    @GetMapping("/{username}")
    public ResponseEntity<User> getUserByUsername(
            @Parameter(description = "Nom d'utilisateur à rechercher")
            @PathVariable String username) {
        Optional<User> user = userService.findUserByUsername(username);
        return user.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Récupérer tous les utilisateurs en ligne",
            description = "Renvoie la liste des utilisateurs actuellement connectés")
    @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    @GetMapping("/online")
    public ResponseEntity<List<User>> getOnlineUsers() {
        List<User> onlineUsers = userService.findAllOnlineUsers();
        return new ResponseEntity<>(onlineUsers, HttpStatus.OK);
    }

    @Operation(summary = "Mettre à jour le statut en ligne d'un utilisateur",
            description = "Change le statut de connexion d'un utilisateur spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statut mis à jour avec succès"),
            @ApiResponse(responseCode = "400", description = "Requête invalide"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<User> updateUserOnlineStatus(
            @Parameter(description = "ID de l'utilisateur")
            @PathVariable Integer id,
            @RequestBody Map<String, Boolean> status) {

        Boolean isOnline = status.get("isOnline");
        if (isOnline == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        try {
            User updatedUser = userService.updateUserOnlineStatus(id, isOnline);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
}