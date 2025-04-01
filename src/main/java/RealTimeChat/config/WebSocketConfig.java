package RealTimeChat.config;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class WebSocketConfig {

    @Value("${socket-server.host}")
    private String host;

    @Value("${socket-server.port}")
    private Integer port;

    @Bean
    public SocketIOServer socketIOServer() {
        Configuration config = new Configuration();
        config.setHostname(host);
        config.setPort(port);

        // CORS => pour pouvoir se connecter depuis n'importe ou, j'ai choisi de mettre all
        //potentiellement on va présenter ca  à distance , j'ai pas envie que ça foire à cause de CORS trop rigides
        config.setOrigin("*");


        // Création du serveur avec la configuration
        final SocketIOServer server = new SocketIOServer(config);

        return server;
    }
}