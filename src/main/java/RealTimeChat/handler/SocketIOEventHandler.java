package RealTimeChat.handler;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;

import RealTimeChat.model.Message;
import RealTimeChat.model.User;
import RealTimeChat.model.ChatRoom;
import RealTimeChat.model.PrivateConversation;
import RealTimeChat.service.MessageService;
import RealTimeChat.service.UserService;
import RealTimeChat.service.ChatRoomService;
import RealTimeChat.service.PrivateConversationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;

@Component
public class SocketIOEventHandler {

    private final SocketIOServer server;
    private final MessageService messageService;
    private final UserService userService;
    private final ChatRoomService chatRoomService;
    private final PrivateConversationService privateConversationService;

    // Map pour stocker les sessions des utilisateurs connectés (userId -> socketId)
    private final Map<Integer, SocketIOClient> userSessions = new ConcurrentHashMap<>();

    @Autowired
    private RealTimeChat.security.JwtUtils jwtUtils;

    @Autowired
    public SocketIOEventHandler(SocketIOServer server,
                                MessageService messageService,
                                UserService userService,
                                ChatRoomService chatRoomService,
                                PrivateConversationService privateConversationService) {
        this.server = server;
        this.messageService = messageService;
        this.userService = userService;
        this.chatRoomService = chatRoomService;
        this.privateConversationService = privateConversationService;

        // Enregistrement des gestionnaires d'événements
        this.server.addConnectListener(this::onConnect);
        this.server.addDisconnectListener(this::onDisconnect);
        this.server.addEventListener("send_message", Message.class, this::onChatMessage);
        this.server.addEventListener("join_room", Integer.class, this::onJoinRoom);
        this.server.addEventListener("leave_room", Integer.class, this::onLeaveRoom);
        this.server.addEventListener("typing", Map.class, this::onTyping);
    }

    // Gestionnaire de connexion client
    @OnConnect
    public void onConnect(SocketIOClient client) {
        System.out.println("Client connecté: " + client.getSessionId());

        // Récupérer le token depuis les paramètres de la requête
        HandshakeData handshakeData = client.getHandshakeData();
        String token = handshakeData.getSingleUrlParam("token");

        try {
            // Extraire l'ID utilisateur du token
            Integer userId = jwtUtils.extractUserId(token);
            if (userId != null) {
                // Associer cet utilisateur à la session socket
                userSessions.put(userId, client);

                // Stocker l'ID utilisateur dans les attributs du client pour référence ultérieure
                client.set("userId", userId);

                // Mettre à jour le statut en ligne
                userService.updateUserOnlineStatus(userId, true);

                // Informer les autres utilisateurs de la connexion
                server.getBroadcastOperations().sendEvent("user_online", userId);
            }
        } catch (Exception e) {
            // En cas d'erreur, déconnecter le client
            System.err.println("Erreur d'authentification à la connexion: " + e.getMessage());
            client.disconnect();
        }
    }

    // Gestionnaire de déconnexion client
    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        System.out.println("Client déconnecté: " + client.getSessionId());

        // Trouver l'utilisateur associé à cette session et marquer comme hors ligne
        userSessions.entrySet().stream()
                .filter(entry -> entry.getValue().equals(client))
                .findFirst()
                .ifPresent(entry -> {
                    try {
                        userService.updateUserOnlineStatus(entry.getKey(), false);
                        userSessions.remove(entry.getKey());

                        // Informer les autres utilisateurs de la déconnexion
                        server.getBroadcastOperations().sendEvent("user_offline", entry.getKey());
                    } catch (Exception e) {
                        System.err.println("Erreur lors de la mise à jour du statut utilisateur: " + e.getMessage());
                    }
                });
    }

    @OnEvent("authenticate")
    public void onAuthenticate(SocketIOClient client, Map<String, Integer> data, AckRequest ackRequest) {

        Integer userId = client.get("userId");
        if (userId != null) {
            try {
                User user = userService.updateUserOnlineStatus(userId, true);

                if (ackRequest.isAckRequested()) {
                    ackRequest.sendAckData("Authentification confirmée", user);
                }
            } catch (Exception e) {
                if (ackRequest.isAckRequested()) {
                    ackRequest.sendAckData("Erreur: " + e.getMessage());
                }
            }
        } else {
            if (ackRequest.isAckRequested()) {
                ackRequest.sendAckData("Erreur: Utilisateur non authentifié");
            }
            client.disconnect();
        }
    }

    // Gestionnaire pour l'envoi de messages
    @OnEvent("send_message")
    public void onChatMessage(SocketIOClient client, Message message, AckRequest ackRequest) {
        try {
            // Déterminer le type de message et le traiter en conséquence
            if (message.getChatRoom() != null || message.getChannelId() != null) {
                handleChatRoomMessage(client, message, ackRequest);
            } else if (message.getRecipient() != null || message.getPrivateConversation() != null) {
                handlePrivateMessage(client, message, ackRequest);
            } else {
                throw new IllegalArgumentException("Le message doit avoir un destinataire (salon ou utilisateur)");
            }
        } catch (Exception e) {
            if (ackRequest.isAckRequested()) {
                ackRequest.sendAckData("Erreur d'envoi de message: " + e.getMessage());
            }
            System.err.println("Erreur d'envoi de message: " + e.getMessage());
        }
    }

    // Gestion des messages de salon
    private void handleChatRoomMessage(SocketIOClient client, Message message, AckRequest ackRequest) {
        // Récupération ou résolution du chat room, comme dans votre code existant...
        if (message.getChatRoom() == null && message.getChannelId() != null) {
            Optional<ChatRoom> chatRoom = chatRoomService.getChatRoomById(message.getChannelId());
            if (chatRoom.isPresent()) {
                message.setChatRoom(chatRoom.get());
            } else {
                System.out.println("❌ ChatRoom not found with ID: " + message.getChannelId());
                throw new IllegalArgumentException("Chat room not found with ID: " + message.getChannelId());
            }
        }

        if (message.getChannelId() == null && message.getChatRoom() != null) {
            message.setChannelId(message.getChatRoom().getId());
            System.out.println("ℹ️ Set channelId from chatRoom: " + message.getChannelId());
        }

        // Nettoyage des champs relatifs aux messages privés
        message.setRecipient(null);
        message.setPrivateConversation(null);

        // Sauvegarde du message dans la base de données
        Message savedMessage = messageService.saveMessage(message);

        // Vérifier que le sender est entièrement chargé
        if (savedMessage.getSender() != null && (savedMessage.getSender().getUsername() == null || savedMessage.getSender().getUsername().isEmpty())) {
            // Recharger l'utilisateur complet
            Optional<User> fullSender = userService.getUserById(savedMessage.getSender().getId());
            fullSender.ifPresent(savedMessage::setSender);
        }

        // Préparation du payload avec le nom de l'expéditeur
        HashMap<String, Object> payload = new HashMap<>();
        payload.put("id", savedMessage.getId());
        payload.put("content", savedMessage.getContent());
        payload.put("senderName", savedMessage.getSender() != null ? savedMessage.getSender().getUsername() : "Unknown");

        // Diffusion du message dans la salle (room)
        String roomId = savedMessage.getChannelId().toString();
        server.getRoomOperations(roomId).sendEvent("new_message", payload);

        System.out.println("✅ Message broadcasted to room, message = " + payload);

        if (ackRequest.isAckRequested()) {
            ackRequest.sendAckData(savedMessage);
            System.out.println("📨 Ack sent");
        }
    }

    // Gestion des messages privés
    private void handlePrivateMessage(SocketIOClient client, Message message, AckRequest ackRequest) {
        User sender = message.getSender();
        User recipient = message.getRecipient();

        if (sender == null) {
            throw new IllegalArgumentException("L'expéditeur du message doit être spécifié");
        }

        if (recipient == null && message.getPrivateConversation() == null) {
            throw new IllegalArgumentException("Le destinataire ou la conversation privée doit être spécifié");
        }

        // Cas 1 : Si le destinataire est fourni mais pas la conversation privée, on la trouve ou la crée
        if (recipient != null && message.getPrivateConversation() == null) {
            PrivateConversation conversation =
                    privateConversationService.findOrCreateConversation(sender.getId(), recipient.getId());
            message.setPrivateConversation(conversation);
        }

        // Cas 2 : Si la conversation privée est fournie mais pas le destinataire, on le déduit de la conversation
        if (message.getPrivateConversation() != null && recipient == null) {
            PrivateConversation conversation = message.getPrivateConversation();
            if (conversation.getUser1().getId().equals(sender.getId())) {
                message.setRecipient(conversation.getUser2());
            } else {
                message.setRecipient(conversation.getUser1());
            }
        }

        // On s'assure que les informations relatives aux salons ne sont pas mélangées
        message.setChatRoom(null);
        message.setChannelId(null);

        // Sauvegarde du message en base
        Message savedMessage = messageService.saveMessage(message);

        // Construction d'un payload simplifié pour éviter les problèmes de sérialisation (références circulaires, etc.)
        HashMap<String, Object> payload = new HashMap<>();
        payload.put("id", savedMessage.getId());
        payload.put("content", savedMessage.getContent());
        payload.put("senderName", savedMessage.getSender() != null ? savedMessage.getSender().getUsername() : "Unknown");
        payload.put("conversationId", savedMessage.getPrivateConversation().getId());

        // Envoi du message au destinataire s'il est connecté
        SocketIOClient recipientClient = userSessions.get(savedMessage.getRecipient().getId());
        if (recipientClient != null) {
            recipientClient.sendEvent("new_message", payload);
        }

        // Envoi également au client émetteur pour la synchro sur plusieurs appareils
        client.sendEvent("new_message", payload);

        // Envoi de l'acknowledgement si demandé
        if (ackRequest.isAckRequested()) {
            ackRequest.sendAckData(savedMessage);
        }
    }

    // Rejoindre un salon de discussion
    @OnEvent("join_room")
    public void onJoinRoom(SocketIOClient client, Integer roomId, AckRequest ackRequest) {
        client.joinRoom(roomId.toString());
        System.out.println("Client " + client.getSessionId() + " a rejoint la room " + roomId);

        if (ackRequest.isAckRequested()) {
            ackRequest.sendAckData("Room " + roomId + " rejointe avec succès");
        }
    }

    // Quitter un salon de discussion
    @OnEvent("leave_room")
    public void onLeaveRoom(SocketIOClient client, Integer roomId, AckRequest ackRequest) {
        client.leaveRoom(roomId.toString());
        System.out.println("Client " + client.getSessionId() + " a quitté la room " + roomId);

        if (ackRequest.isAckRequested()) {
            ackRequest.sendAckData("Room " + roomId + " quittée avec succès");
        }
    }

    // Gestion de l'événement "typing" (utilisateur en train d'écrire)
    @OnEvent("typing")
    public void onTyping(SocketIOClient client, Map<String, Object> data, AckRequest ackRequest) {
        Integer userId = (Integer) data.get("userId");
        Integer roomId = (Integer) data.get("roomId");
        Integer recipientId = (Integer) data.get("recipientId");
        Boolean isTyping = (Boolean) data.get("isTyping");

        if (roomId != null) {
            // Notification de frappe dans un canal
            server.getRoomOperations(roomId.toString()).sendEvent("typing", data);
        } else if (recipientId != null) {
            // Notification de frappe en privé
            SocketIOClient recipientClient = userSessions.get(recipientId);
            if (recipientClient != null) {
                recipientClient.sendEvent("typing", data);
            }
        }
    }
}