package RealTimeChat.repository;

import RealTimeChat.model.Message;
import RealTimeChat.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
    List<Message> findByChatRoomIdOrderBySentAtAsc(Integer chatRoomId);

    List<Message> findByPrivateConversationIdOrderBySentAtAsc(Integer privateConversationId);

    List<Message> findBySenderAndRecipientOrderBySentAtAsc(User sender, User recipient);

    List<Message> findByRecipientAndIsReadFalse(User recipient);

    // Parfois spring ne peut pas gérer certaines requetes il faut les faire manuellement du coup faut foutre @Query et faire notre petite affaire
    @Query(value = "SELECT * FROM messages WHERE chat_room_id = :chatRoomId ORDER BY created_at DESC LIMIT :limit",
            nativeQuery = true)
    List<Message> findLastMessagesByChatRoomId(@Param("chatRoomId") Integer chatRoomId, @Param("limit") int limit);

    @Query(value = "SELECT * FROM messages WHERE private_conversation_id = :conversationId ORDER BY created_at DESC LIMIT :limit",
            nativeQuery = true)
    List<Message> findLastMessagesByPrivateConversationId(@Param("conversationId") Integer conversationId, @Param("limit") int limit);

    long countByRecipientAndIsReadFalse(User recipient);
}