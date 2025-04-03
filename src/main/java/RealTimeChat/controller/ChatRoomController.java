package RealTimeChat.controller;

import RealTimeChat.dto.ChatRoomMemberRequest;
import RealTimeChat.model.ChatRoom;
import RealTimeChat.model.ChatRoomMember;
import RealTimeChat.model.User;
import RealTimeChat.service.ChatRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<ChatRoom> createChatRoom(@RequestBody ChatRoom chatRoom) {
        try {
            ChatRoom created = chatRoomService.createChatRoom(chatRoom);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (Exception e) {
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
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChatRoom(@PathVariable Integer id) {
        try {
            chatRoomService.deleteChatRoom(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Lister tous les utilisateurs du salon")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users and chat room found"),
            @ApiResponse(responseCode = "404", description = "Chat room not found")
    })
    @GetMapping("/{id}/user")
    public ResponseEntity<List<User>> getAllUsersByChatRooms(@PathVariable Integer id) {
        try {
            List<User> users = chatRoomService.getAllUsersByChatRoom(id);
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Ajouter un utilisateur à un salon")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Chat room member added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid data")
    })
    @PostMapping("/{id}/user")
    public ResponseEntity<List<User>> addUserToChatRooms(@PathVariable Integer id, ChatRoomMemberRequest request) {
        try {
            ChatRoomMember chatRoomMember = chatRoomService.addChatRoomMember(id, request.getUserId());
            List<User> users = chatRoomService.getAllUsersByChatRoom(id);
            return new ResponseEntity<>(users, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
