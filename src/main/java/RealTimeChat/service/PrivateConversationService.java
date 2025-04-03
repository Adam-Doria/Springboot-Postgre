package RealTimeChat.service;

import RealTimeChat.model.PrivateConversation;
import RealTimeChat.model.User;
import RealTimeChat.repository.PrivateConversationRepository;
import RealTimeChat.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PrivateConversationService {

    @Autowired
    private PrivateConversationRepository privateConversationRepository;

    @Autowired
    private UserRepository userRepository;

    public PrivateConversation createPrivateConversation(PrivateConversation conversation) {
        return privateConversationRepository.save(conversation);
    }

    public Optional<PrivateConversation> getPrivateConversationById(Integer id) {
        return privateConversationRepository.findById(id);
    }

    public List<PrivateConversation> getAllPrivateConversations() {
        return privateConversationRepository.findAll();
    }

    public PrivateConversation updatePrivateConversation(PrivateConversation conversation) {
        return privateConversationRepository.save(conversation);
    }

    public void deletePrivateConversation(Integer id) {
        privateConversationRepository.deleteById(id);
    }

    /**
     * Trouve une conversation privée entre deux utilisateurs, peu importe l'ordre des utilisateurs
     * (user1 peut être le premier ou le deuxième utilisateur dans la conversation)
     */
    public Optional<PrivateConversation> getPrivateConversationByUsers(Integer userId1, Integer userId2) {
        User user1 = userRepository.findById(userId1)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + userId1));
        User user2 = userRepository.findById(userId2)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + userId2));

        // Cherche la conversation dans les deux sens (user1->user2 ou user2->user1)
        Optional<PrivateConversation> conversation = privateConversationRepository.findByUser1AndUser2(user1, user2);
        if (conversation.isPresent()) {
            return conversation;
        }

        return privateConversationRepository.findByUser1AndUser2(user2, user1);
    }

    @Transactional
    public PrivateConversation findOrCreateConversation(Integer user1Id, Integer user2Id) {
        Optional<PrivateConversation> existingConversation = getPrivateConversationByUsers(user1Id, user2Id);

        if (existingConversation.isPresent()) {
            return existingConversation.get();
        }

        User user1 = userRepository.findById(user1Id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + user1Id));
        User user2 = userRepository.findById(user2Id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + user2Id));

        PrivateConversation newConversation = new PrivateConversation();
        newConversation.setUser1(user1);
        newConversation.setUser2(user2);
        newConversation.setCreatedAt(LocalDateTime.now());
        newConversation.setUpdatedAt(LocalDateTime.now());

        return privateConversationRepository.save(newConversation);
    }

    public List<PrivateConversation> getAllConversationsForUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + userId));

        return privateConversationRepository.findByUser1OrUser2(user, user);
    }
}

