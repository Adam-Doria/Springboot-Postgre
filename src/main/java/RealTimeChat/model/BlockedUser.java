package RealTimeChat.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name="blocked_users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entité représentant les utilisateurs bloqué")
public class BlockedUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identifiant unique auto-généré")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"password", "email", "createdAt", "updatedAt"})
    @Schema(description = "Utilisateur bloqué")
    private User user;


    @ManyToOne
    @JoinColumn(name = "chat_room_id", nullable = false)
    @Schema(description = "Salon de discussion où l'utilisateur a été bloqué")
    private ChatRoom chatRoom;

    @Column(name = "unban_date")
    @Schema(description = "Date à laquelle l'utilisateur sera débloqué (peut être nulle)")
    private LocalDateTime unbanDate;
}
