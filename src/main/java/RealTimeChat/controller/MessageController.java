package RealTimeChat.controller;

import RealTimeChat.model.Message;
import RealTimeChat.model.User;
import RealTimeChat.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Operation(summary = "Envoyer un message", description = "Enregistre un nouveau message dans la base de données")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Message créé",
                    content = @Content(schema = @Schema(implementation = Message.class))),
            @ApiResponse(responseCode = "400", description = "Requête invalide")
    })
    @PostMapping
    public ResponseEntity<Message> sendMessage(@RequestBody Message message) {
        try {
            Message savedMessage = messageService.saveMessage(message);
            return new ResponseEntity<>(savedMessage, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Récupérer les messages d'un salon de discussion", description = "Renvoie la liste des messages d'un chat room par ordre d'envoi")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès",
                    content = @Content(schema = @Schema(implementation = Message.class))),
            @ApiResponse(responseCode = "404", description = "Salon non trouvé")
    })
    @GetMapping("/chat/{chatRoomId}")
    public ResponseEntity<List<Message>> getChatRoomMessages(@PathVariable Integer chatRoomId) {
        try {
            List<Message> messages = messageService.getChatRoomMessages(chatRoomId);
            return new ResponseEntity<>(messages, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Récupérer les messages d'une conversation privée", description = "Renvoie la liste des messages d'une conversation privée par ordre d'envoi")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès",
                    content = @Content(schema = @Schema(implementation = Message.class))),
            @ApiResponse(responseCode = "404", description = "Conversation non trouvée")
    })
    @GetMapping("/private/{conversationId}")
    public ResponseEntity<List<Message>> getPrivateConversationMessages(@PathVariable Integer conversationId) {
        try {
            List<Message> messages = messageService.getPrivateConversationMessages(conversationId);
            return new ResponseEntity<>(messages, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Marquer un message comme lu", description = "Met à jour l'état du message pour indiquer qu'il a été lu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message mis à jour avec succès",
                    content = @Content(schema = @Schema(implementation = Message.class))),
            @ApiResponse(responseCode = "404", description = "Message non trouvé")
    })
    @PutMapping("/{messageId}/read")
    public ResponseEntity<Message> markMessageAsRead(@PathVariable Integer messageId) {
        try {
            Message updatedMessage = messageService.markAsRead(messageId);
            return new ResponseEntity<>(updatedMessage, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Editer un message", description = "Modifie le contenu d'un message existant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message édité avec succès",
                    content = @Content(schema = @Schema(implementation = Message.class))),
            @ApiResponse(responseCode = "404", description = "Message non trouvé"),
            @ApiResponse(responseCode = "400", description = "Requête invalide")
    })
    @PutMapping("/{messageId}")
    public ResponseEntity<Message> editMessage(@PathVariable Integer messageId,
                                               @RequestBody Map<String, String> payload) {
        try {
            String newContent = payload.get("newContent");
            if(newContent == null || newContent.trim().isEmpty()){
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            Message updatedMessage = messageService.editMessage(messageId, newContent);
            return new ResponseEntity<>(updatedMessage, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Supprimer un message", description = "Supprime un message en fonction de son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Message non trouvé")
    })
    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Integer messageId) {
        try {
            messageService.deleteMessage(messageId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
