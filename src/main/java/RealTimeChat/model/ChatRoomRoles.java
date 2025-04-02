package RealTimeChat.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name="chat_room_role")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entité représentant les roles des membres des chats room")
public class ChatRoomRoles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identifiant unique auto-généré")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"password", "email", "createdAt", "updatedAt"})
    @Schema(description = "Utilisateur")
    private User user;


    @ManyToOne
    @JoinColumn(name = "chat_room_id", nullable = false)
    @Schema(description = "Salon de discussion")
    private ChatRoom chatRoom;

    @Column(name = "role_name", nullable = false)
    @Schema(description = "Nom du role")
    private String name;
}
