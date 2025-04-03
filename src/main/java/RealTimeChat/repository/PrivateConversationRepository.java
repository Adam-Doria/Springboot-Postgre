package RealTimeChat.repository;

import RealTimeChat.model.PrivateConversation;
import RealTimeChat.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrivateConversationRepository extends JpaRepository<PrivateConversation, Integer> {

    Optional<PrivateConversation> findByUser1AndUser2(User user1, User user2);

    List<PrivateConversation> findByUser1OrUser2(User user1, User user2);

    // Si on se chauffe avec les notifications c'est bon de savoir combien y'a de message non lus
    @Query("SELECT COUNT(m) FROM Message m WHERE m.privateConversation IN " +
            "(SELECT pc FROM PrivateConversation pc WHERE pc.user1 = :user OR pc.user2 = :user) " +
            "AND m.recipient = :user AND m.isRead = false")
    long countUnreadMessagesByUser(@Param("user") User user);
}
