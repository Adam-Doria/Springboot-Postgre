package RealTimeChat.service;

import RealTimeChat.model.PrivateConversation;
import RealTimeChat.repository.PrivateConversationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PrivateConversationService {

    @Autowired
    private PrivateConversationRepository privateConversationRepository;

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
}
