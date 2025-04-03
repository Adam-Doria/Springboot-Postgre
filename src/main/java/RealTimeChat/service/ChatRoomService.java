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
        return chatRoomRepository.save(chatRoom);
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

    /**
     * Vérifie si un utilisateur est l'administrateur d'un salon de discussion
     * @param chatRoomId L'identifiant du salon de discussion
     * @param userId L'identifiant de l'utilisateur
     * @return true si l'utilisateur est l'administrateur, false sinon
     */
    public boolean isUserChatRoomAdmin(Integer chatRoomId, Integer userId) {
        Optional<ChatRoom> chatRoomOpt = getChatRoomById(chatRoomId);
        if (chatRoomOpt.isPresent()) {
            ChatRoom chatRoom = chatRoomOpt.get();
            return chatRoom.getAdminId() != null && chatRoom.getAdminId().equals(userId);
        }
        return false;
    }

    public List<User> getAllUsersByChatRoom(Integer id) {return chatRoomMemberRepository.findUsersByChatRoomId(id);}
}
