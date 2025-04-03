package RealTimeChat.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="chat_rooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entité représentant un salon de discussion")
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identifiant unique auto-généré du salon")
    private Integer id;

    @Column(nullable = false)
    @Schema(description = "Nom du salon", example = "Général")
    private String name;

    @Column
    @Schema(description = "Description du salon", example = "Discussions générales")
    private String description;

    @Column(name = "admin_id")
    @Schema(description = "ID de l'administrateur du salon")
    private Integer adminId;

    @Column(name = "created_at")
    @Schema(description = "Date et heure de création du salon")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @Schema(description = "Date et heure de dernière mise à jour du salon")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    @Schema(description = "Liste des messages associés à ce salon")
    private List<Message> messages = new ArrayList<>();

    // Constructeur personnalisé pour créer un salon avec un admin spécifié
    public ChatRoom(String name, String description, Integer adminId) {
        this.name = name;
        this.description = description;
        this.adminId = adminId;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
        
        // Debug pour comprendre le problème
        System.out.println("ChatRoom.prePersist - adminId: " + adminId);
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}