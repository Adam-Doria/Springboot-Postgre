package RealTimeChat.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="private_conversations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entité représentant une conversation privée entre deux utilisateurs")
public class PrivateConversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identifiant unique auto-généré de la conversation")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user1_id", nullable = false)
    @Schema(description = "Premier utilisateur de la conversation")
    private User user1;

    @ManyToOne
    @JoinColumn(name = "user2_id", nullable = false)
    @Schema(description = "Second utilisateur de la conversation")
    private User user2;

    @Column(name = "created_at")
    @Schema(description = "Date et heure de création de la conversation")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @Schema(description = "Date et heure de dernière mise à jour de la conversation")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "privateConversation", cascade = CascadeType.ALL, orphanRemoval = true)
    @Schema(description = "Liste des messages associés à cette conversation privée")
    @JsonIgnore
    private List<Message> messages = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}