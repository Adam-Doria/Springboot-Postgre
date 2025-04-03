package RealTimeChat.service;

import RealTimeChat.model.Message;
import RealTimeChat.model.User;
import RealTimeChat.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

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


    public List<Message> getMessagesBetweenUsers(User user1, User user2) {
        List<Message> messagesFromUser1 = messageRepository.findBySenderAndRecipientOrderBySentAtAsc(user1, user2);
        List<Message> messagesFromUser2 = messageRepository.findBySenderAndRecipientOrderBySentAtAsc(user2, user1);

        return Stream.concat(messagesFromUser1.stream(), messagesFromUser2.stream())
                .sorted(Comparator.comparing(Message::getSentAt))
                .collect(Collectors.toList());
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