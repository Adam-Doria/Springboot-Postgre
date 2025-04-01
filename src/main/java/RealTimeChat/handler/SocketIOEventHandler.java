package RealTimeChat.handler;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;

import RealTimeChat.model.Message;
import RealTimeChat.model.User;
import RealTimeChat.service.MessageService;
import RealTimeChat.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SocketIOEventHandler {

    private final SocketIOServer server;
    private final MessageService messageService;
    private final UserService userService;

    // Map pour stocker les sessions des utilisateurs connectés (userId -> socketId)
    private final Map<Integer, SocketIOClient> userSessions = new ConcurrentHashMap<>();

    @Autowired
    public SocketIOEventHandler(SocketIOServer server, MessageService messageService, UserService userService) {
        this.server = server;
        this.messageService = messageService;
        this.userService = userService;

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
        // Remarque: l'authentification et l'association utilisateur-session se fait via des événements spécifiques
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

    // Gestionnaire pour l'authentification utilisateur
    @OnEvent("authenticate")
    public void onAuthenticate(SocketIOClient client, Map<String, Integer> data, AckRequest ackRequest) {
        Integer userId = data.get("userId");

        if (userId != null) {
            try {
                // Associer cet utilisateur à la session socket
                userSessions.put(userId, client);

                // Mettre à jour le statut en ligne
                User user = userService.updateUserOnlineStatus(userId, true);

                // Informer les autres utilisateurs de la connexion
                server.getBroadcastOperations().sendEvent("user_online", userId);

                // Confirmer l'authentification au client
                if (ackRequest.isAckRequested()) {
                    ackRequest.sendAckData("Authentification réussie", user);
                }
            } catch (Exception e) {
                if (ackRequest.isAckRequested()) {
                    ackRequest.sendAckData("Erreur d'authentification: " + e.getMessage());
                }
                System.err.println("Erreur d'authentification: " + e.getMessage());
            }
        }
    }

    // Gestionnaire pour l'envoi de messages
    @OnEvent("send_message")
    public void onChatMessage(SocketIOClient client, Message message, AckRequest ackRequest) {
        try {
            // Sauvegarder le message dans la base de données
            Message savedMessage = messageService.saveMessage(message);

            // Diffuser le message selon son type
            if (message.getChannelId() != null) {
                // Message de canal: envoyer à tous les clients dans la room
                server.getRoomOperations(message.getChannelId().toString()).sendEvent("new_message", savedMessage);
            } else if (message.getRecipient() != null) {
                // Message privé: envoyer au destinataire et à l'expéditeur
                // Trouver la session du destinataire
                SocketIOClient recipientClient = userSessions.get(message.getRecipient().getId());
                if (recipientClient != null) {
                    recipientClient.sendEvent("new_message", savedMessage);
                }
                // Aussi envoyer à l'expéditeur pour synchro sur plusieurs appareils
                client.sendEvent("new_message", savedMessage);
            }

            // Accusé de réception
            if (ackRequest.isAckRequested()) {
                ackRequest.sendAckData(savedMessage);
            }
        } catch (Exception e) {
            if (ackRequest.isAckRequested()) {
                ackRequest.sendAckData("Erreur d'envoi de message: " + e.getMessage());
            }
            System.err.println("Erreur d'envoi de message: " + e.getMessage());
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