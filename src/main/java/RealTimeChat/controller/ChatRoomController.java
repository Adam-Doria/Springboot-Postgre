package RealTimeChat.controller;

import RealTimeChat.model.ChatRoom;
import RealTimeChat.model.User;
import RealTimeChat.service.ChatRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/chatrooms")
public class ChatRoomController {

    @Autowired
    private ChatRoomService chatRoomService;

    @Operation(summary = "Créer un salon de discussion")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Chat room created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid data")
    })
    @PostMapping
    public ResponseEntity<ChatRoom> createChatRoom(@RequestBody ChatRoom chatRoomRequest) {
        try {
            // Récupérer l'ID de l'utilisateur à partir du token JWT
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Integer userId = Integer.parseInt(authentication.getName());
            
            System.out.println("DEBUG - UserId extrait du token: " + userId);
            
            // Créer un nouveau salon en utilisant le constructeur personnalisé
            ChatRoom chatRoom = new ChatRoom(
                chatRoomRequest.getName(),
                chatRoomRequest.getDescription(),
                userId  // Définir l'utilisateur courant comme administrateur
            );
            
            System.out.println("DEBUG - AdminId du salon avant création: " + chatRoom.getAdminId());
            
            // Sauvegarder le salon
            ChatRoom created = chatRoomService.createChatRoom(chatRoom);
            
            System.out.println("DEBUG - AdminId du salon après création: " + created.getAdminId());
            
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Récupérer un salon de discussion par ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chat room found"),
            @ApiResponse(responseCode = "404", description = "Chat room not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ChatRoom> getChatRoomById(@PathVariable Integer id) {
        return chatRoomService.getChatRoomById(id)
                .map(chatRoom -> new ResponseEntity<>(chatRoom, HttpStatus.OK))
                .orElse(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Lister tous les salons de discussion")
    @GetMapping
    public ResponseEntity<List<ChatRoom>> getAllChatRooms() {
        List<ChatRoom> chatRooms = chatRoomService.getAllChatRooms();
        return new ResponseEntity<>(chatRooms, HttpStatus.OK);
    }

    @Operation(summary = "Supprimer un salon de discussion par ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chat room deleted successfully"),
            @ApiResponse(responseCode = "403", description = "User is not the admin of this chat room"),
            @ApiResponse(responseCode = "404", description = "Chat room not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteChatRoom(@PathVariable Integer id) {
        try {
            // Récupérer l'ID de l'utilisateur à partir du token JWT
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Integer userId = Integer.parseInt(authentication.getName());
            
            // Récupérer le salon
            ChatRoom chatRoom = chatRoomService.getChatRoomById(id)
                    .orElseThrow(() -> new RuntimeException("Salon non trouvé avec l'ID: " + id));
            
            // Vérifier si l'utilisateur est l'administrateur du salon
            if (!userId.equals(chatRoom.getAdminId())) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Vous n'êtes pas autorisé à supprimer ce salon");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
            
            chatRoomService.deleteChatRoom(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Lister tous les utilisateurs du salon")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users and chat room found"),
            @ApiResponse(responseCode = "404", description = "Chat room not found")
    })
    @GetMapping("/{id}/users")
    public ResponseEntity<List<User>> getAllUsersByChatRooms(@PathVariable Integer id) {
        try {
            List<User> users = chatRoomService.getAllUsersByChatRoom(id);
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
