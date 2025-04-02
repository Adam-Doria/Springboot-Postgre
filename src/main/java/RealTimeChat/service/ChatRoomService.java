package RealTimeChat.service;

import RealTimeChat.model.ChatRoom;
import RealTimeChat.repository.ChatRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChatRoomService {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

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

    public void getAllUsersByChatRoom(Integer id) {chatRoomRepository}
}
