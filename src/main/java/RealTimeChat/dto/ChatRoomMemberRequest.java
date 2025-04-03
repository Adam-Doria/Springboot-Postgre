package RealTimeChat.dto;
import io.swagger.v3.oas.annotations.media.Schema;

public class ChatRoomMemberRequest {
    @Schema(description = "ID de l'utilisateur à ajouter au salon", example = "11", required = true)
    private Integer userId;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
