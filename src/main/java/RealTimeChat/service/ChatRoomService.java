package RealTimeChat.service;

import RealTimeChat.model.ChatRoom;
import RealTimeChat.model.ChatRoomMember;
import RealTimeChat.model.User;
import RealTimeChat.repository.ChatRoomMemberRepository;
import RealTimeChat.repository.ChatRoomRepository;
import RealTimeChat.repository.UserRepository;
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
    @Autowired
    private UserRepository userRepository;

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

    public List<User> getAllUsersByChatRoom(Integer id) {return chatRoomMemberRepository.findUsersByChatRoomId(id);}

    public ChatRoomMember addChatRoomMember(Integer chatRoomId, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new RuntimeException("ChatRoom not found"));

        ChatRoomMember chatRoomMember = new ChatRoomMember();
        chatRoomMember.setUser(user);
        chatRoomMember.setChatRoom(chatRoom);

        return chatRoomMemberRepository.save(chatRoomMember);
    }
}
