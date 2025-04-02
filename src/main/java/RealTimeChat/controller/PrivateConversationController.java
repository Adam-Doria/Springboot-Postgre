package RealTimeChat.controller;

import RealTimeChat.model.PrivateConversation;
import RealTimeChat.service.PrivateConversationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/private-conversations")
public class PrivateConversationController {

    @Autowired
    private PrivateConversationService privateConversationService;

    @Operation(summary = "Créer une conversation privée")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Conversation créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PostMapping
    public ResponseEntity<PrivateConversation> createPrivateConversation(@RequestBody PrivateConversation conversation) {
        try {
            PrivateConversation created = privateConversationService.createPrivateConversation(conversation);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Récupérer une conversation privée par ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conversation trouvée"),
            @ApiResponse(responseCode = "404", description = "Conversation non trouvée")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PrivateConversation> getPrivateConversationById(@PathVariable Integer id) {
        return privateConversationService.getPrivateConversationById(id)
                .map(conversation -> new ResponseEntity<>(conversation, HttpStatus.OK))
                .orElse(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Lister toutes les conversations privées")
    @GetMapping
    public ResponseEntity<List<PrivateConversation>> getAllPrivateConversations() {
        List<PrivateConversation> conversations = privateConversationService.getAllPrivateConversations();
        return new ResponseEntity<>(conversations, HttpStatus.OK);
    }

    @Operation(summary = "Mettre à jour une conversation privée")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conversation mise à jour avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Conversation non trouvée")
    })
    @PutMapping("/{id}")
    public ResponseEntity<PrivateConversation> updatePrivateConversation(@PathVariable Integer id, @RequestBody PrivateConversation conversation) {
        return privateConversationService.getPrivateConversationById(id)
                .map(existingConversation -> {
                    conversation.setId(id);
                    PrivateConversation updated = privateConversationService.updatePrivateConversation(conversation);
                    return new ResponseEntity<>(updated, HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Supprimer une conversation privée par ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrivateConversation(@PathVariable Integer id) {
        try {
            privateConversationService.deletePrivateConversation(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
