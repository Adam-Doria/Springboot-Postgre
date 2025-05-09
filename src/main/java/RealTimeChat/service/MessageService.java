package RealTimeChat.service;

import RealTimeChat.model.Message;
import RealTimeChat.model.User;
import RealTimeChat.repository.MessageRepository;
import RealTimeChat.service.PrivateConversationService;
import RealTimeChat.model.PrivateConversation;
import RealTimeChat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private ChatRoomService chatRoomService;


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PrivateConversationService privateConversationService;

    @Transactional
    public Message saveMessage(Message message) {
        return messageRepository.save(message);
    }

    public List<Message> getChatRoomMessages(Integer chatRoomId) {
        return messageRepository.findByChatRoomIdOrderBySentAtAsc(chatRoomId);
    }

    public List<Message> getLastChatRoomMessages(Integer chatRoomId, int limit) {
        List<Message> messages = messageRepository.findLastMessagesByChatRoomId(chatRoomId, limit);

        return messages.stream()
                .sorted(Comparator.comparing(Message::getSentAt))
                .collect(Collectors.toList());
    }


    public List<Message> getPrivateConversationMessages(Integer privateConversationId) {
        return messageRepository.findByPrivateConversationIdOrderBySentAtAsc(privateConversationId);
    }

    public List<Message> getLastPrivateConversationMessages(Integer privateConversationId, int limit) {
        List<Message> messages = messageRepository.findLastMessagesByPrivateConversationId(privateConversationId, limit);
        return messages.stream()
                .sorted(Comparator.comparing(Message::getSentAt))
                .collect(Collectors.toList());
    }

    public List<Message> getMessagesBetweenUsers(Integer userId1, Integer userId2) {
        User user1 = userRepository.findById(userId1)
                .orElseThrow(() -> new RuntimeException("Utilisateur 1 non trouvé"));
        User user2 = userRepository.findById(userId2)
                .orElseThrow(() -> new RuntimeException("Utilisateur 2 non trouvé"));

        PrivateConversation conversation = privateConversationService.findOrCreateConversation(userId1, userId2);


        return messageRepository.findByPrivateConversationIdOrderBySentAtAsc(conversation.getId());
    }


    public List<Message> getUnreadMessages(User user) {
        return messageRepository.findByRecipientAndIsReadFalse(user);
    }


    public long countUnreadMessages(User user) {
        return messageRepository.countByRecipientAndIsReadFalse(user);
    }

    @Transactional
    public Message markAsRead(Integer messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message non trouvé avec l'ID: " + messageId));
        message.setIsRead(true);
        message.setUpdatedAt(LocalDateTime.now());
        return messageRepository.save(message);
    }

    @Transactional
    public void markAllAsRead(User recipient, User sender) {
        List<Message> unreadMessages = messageRepository.findBySenderAndRecipientOrderBySentAtAsc(sender, recipient)
                .stream()
                .filter(message -> !message.getIsRead())
                .collect(Collectors.toList());

        for (Message message : unreadMessages) {
            message.setIsRead(true);
            message.setUpdatedAt(LocalDateTime.now());
        }

        messageRepository.saveAll(unreadMessages);
    }

    @Transactional
    public Message editMessage(Integer messageId, String newContent) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message non trouvé avec l'ID: " + messageId));
        message.setContent(newContent);
        message.setIsEdited(true);
        // La mise à jour de updatedAt se fait automatiquement via @PreUpdate
        return messageRepository.save(message);
    }

    /**
     * Vérifie si un utilisateur est autorisé à supprimer un message.
     * Un utilisateur peut supprimer un message s'il en est l'expéditeur ou
     * s'il est l'administrateur du salon dans lequel le message a été posté.
     * 
     * @param messageId L'ID du message à vérifier
     * @param userId L'ID de l'utilisateur qui tente de supprimer le message
     * @return true si l'utilisateur est autorisé à supprimer le message, false sinon
     */
    public boolean canDeleteMessage(Integer messageId, Integer userId) {
        Optional<Message> messageOpt = messageRepository.findById(messageId);
        
        if (messageOpt.isEmpty()) {
            return false;
        }
        
        Message message = messageOpt.get();
        
        // L'expéditeur du message peut toujours le supprimer
        if (message.getSender() != null && message.getSender().getId().equals(userId)) {
            return true;
        }
        
        // Si le message est dans un salon, vérifier si l'utilisateur est l'admin du salon
        if (message.getChatRoom() != null) {
            return chatRoomService.isUserChatRoomAdmin(message.getChatRoom().getId(), userId);
        }
        
        // Dans les autres cas (messages privés), seul l'expéditeur peut supprimer
        return false;
    }

    @Transactional
    public void deleteMessage(Integer messageId) {
        messageRepository.deleteById(messageId);
    }

    /**
     * Supprime un message si l'utilisateur est l'auteur du message
     * @param messageId ID du message à supprimer
     * @param userId ID de l'utilisateur qui tente de supprimer le message
     * @return true si le message a été supprimé, false si l'utilisateur n'est pas l'auteur
     * @throws RuntimeException si le message n'est pas trouvé
     */
    @Transactional
    public boolean deleteOwnMessage(Integer messageId, Integer userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message non trouvé avec l'ID: " + messageId));
        
        // Vérifier si l'utilisateur est l'auteur du message
        if (message.getSender() != null && message.getSender().getId().equals(userId)) {
            messageRepository.delete(message);
            return true;
        }
        
        return false;
    }
}