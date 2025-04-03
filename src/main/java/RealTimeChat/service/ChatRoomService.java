package RealTimeChat.service;

import RealTimeChat.model.ChatRoom;
import RealTimeChat.model.User;
import RealTimeChat.repository.ChatRoomMemberRepository;
import RealTimeChat.repository.ChatRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChatRoomService {

    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private ChatRoomMemberRepository chatRoomMemberRepository;

    public ChatRoom createChatRoom(ChatRoom chatRoom) {
        // Affichons des logs pour déboguer
        System.out.println("ChatRoomService - createChatRoom - adminId avant sauvegarde: " + chatRoom.getAdminId());
        
        // Vérifier que l'adminId est bien défini
        if (chatRoom.getAdminId() == null) {
            System.out.println("ChatRoomService - ATTENTION: adminId était null!");
        }
        
        // Sauvegarde du salon
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        
        // Vérifions après sauvegarde
        System.out.println("ChatRoomService - createChatRoom - adminId après sauvegarde: " + savedChatRoom.getAdminId());
        
        return savedChatRoom;
    }

    public Optional<ChatRoom> getChatRoomById(Integer id) {
        return chatRoomRepository.findById(id);
    }

    public List<ChatRoom> getAllChatRooms() {
        return chatRoomRepository.findAll();
    }

    public ChatRoom updateChatRoom(ChatRoom chatRoom) {
        return chatRoomRepository.save(chatRoom);
    }

    public void deleteChatRoom(Integer id) {
        chatRoomRepository.deleteById(id);
    }

    public List<User> getAllUsersByChatRoom(Integer id) {return chatRoomMemberRepository.findUsersByChatRoomId(id);}
}
