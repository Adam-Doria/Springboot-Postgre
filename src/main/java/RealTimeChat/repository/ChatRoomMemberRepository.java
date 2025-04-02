package RealTimeChat.repository;

import RealTimeChat.model.ChatRoomMember;
import RealTimeChat.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Integer> {
    @Query("SELECT m.user FROM ChatRoomMember m WHERE m.chatRoom.id = :chatRoomId")
    List<User> findUsersByChatRoomId(@Param("chatRoomId") Integer chatRoomId);
}
