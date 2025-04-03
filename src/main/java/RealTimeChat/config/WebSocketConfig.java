package RealTimeChat.config;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.AuthorizationListener;
import com.corundumstudio.socketio.HandshakeData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import RealTimeChat.security.JwtUtils;

@Component
public class WebSocketConfig {

    @Value("${socket-server.host}")
    private String host;

    @Value("${socket-server.port}")
    private Integer port;

    @Autowired
    private JwtUtils jwtUtils;

    @Bean
    public SocketIOServer socketIOServer() {
        Configuration config = new Configuration();
        config.setHostname(host);
        config.setPort(port);
        config.setOrigin("*");
        config.setAllowHeaders("*");

        // Ajout de l'authentification à la connexion
        config.setAuthorizationListener(new AuthorizationListener() {
            @Override
            public boolean isAuthorized(HandshakeData data) {
                // Récupération du token depuis les paramètres de la requête
                String token = data.getSingleUrlParam("token");
                if (token != null) {
                    try {
                        Integer userId = jwtUtils.extractUserId(token);
                        return userId != null;
                    } catch (Exception e) {
                        return false;
                    }
                }
                return false;
            }
        });

        final SocketIOServer server = new SocketIOServer(config);
        return server;
    }
}