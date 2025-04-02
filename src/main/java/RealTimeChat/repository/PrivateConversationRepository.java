package RealTimeChat.repository;

import RealTimeChat.model.PrivateConversation;
import RealTimeChat.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrivateConversationRepository extends JpaRepository<PrivateConversation, Integer> {

    Optional<PrivateConversation> findByUser1AndUser2(User user1, User user2);

    List<PrivateConversation> findByUser1OrUser2(User user1, User user2);
}
