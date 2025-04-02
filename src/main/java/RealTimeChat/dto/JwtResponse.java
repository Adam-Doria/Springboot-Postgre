package RealTimeChat.dto;

import lombok.Getter;
import lombok.AllArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Getter
@AllArgsConstructor
public class JwtResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = -8091879091924046844L;

    private final String token;
    private final Integer userId;
    private final String username;
    private final String displayName;
    private final boolean isOnline;
}