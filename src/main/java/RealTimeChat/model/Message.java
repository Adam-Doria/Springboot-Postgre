package RealTimeChat.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Entity
@Table(name="messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entité représentant un message de chat")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 2000)
    @Schema(description = "Contenu du message", example = "Bonjour tout le monde!")
    private String content;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    @JsonIgnoreProperties({"password", "email", "createdAt", "updatedAt"})
    @Schema(description = "Utilisateur qui a envoyé le message")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "recipient_id")
    @JsonIgnoreProperties({"password", "email", "createdAt", "updatedAt"})
    @Schema(description = "Utilisateur destinataire (pour messages privés)")
    private User recipient;


    @ManyToOne
    @JoinColumn(name = "chat_room_id")
    @Schema(description = "Salon de discussion où le message a été envoyé (null pour messages privés)")
    private ChatRoom chatRoom;

    @Column(name = "private_conversation_id")
    @Schema(description = "ID de la conversation privée (null pour messages de salon)")
    private Integer privateConversationId;


    @Column(name = "is_edited")
    @Schema(description = "Indique si le message a été modifié", example = "false")
    private Boolean isEdited;

    @Schema(description = "Indique si le message a été lu", example = "false")
    private Boolean isRead;

    @Column(name = "created_at")
    @Schema(description = "Date et heure d'envoi du message")
    private LocalDateTime sentAt;

    @Column(name = "updated_at")
    @Schema(description = "Date et heure de dernière mise à jour du message")
    private LocalDateTime updatedAt;

    // Ce champ est utilisé uniquement pour Socket.IO, on le garde pas en base de donnée, du coup on fou
    // @transient pour l'expliciter
    @Transient
    @Schema(description = "ID du canal pour l'envoi par Socket.IO (non persisté)")
    private Integer channelId;

    @PrePersist
    public void prePersist() {
        if (sentAt == null) {
            sentAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
        if (isRead == null) {
            isRead = false;
        }
        if (isEdited == null) {
            isEdited = false;
        }

        if (chatRoom != null && channelId == null) {
            channelId = chatRoom.getId();
        }

        if (chatRoom == null && recipient == null && privateConversationId == null) {
            throw new IllegalStateException("Un message doit avoir au moins un destinataire (salon de discussion ou utilisateur)");
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
        isEdited = true;
    }

}