package RealTimeChat.config;

import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import jakarta.annotation.PreDestroy;

@Component
public class SocketIOServerInitializer implements CommandLineRunner {

    private final SocketIOServer server;

    @Autowired
    public SocketIOServerInitializer(SocketIOServer server) {
        this.server = server;
    }


    @Override
    public void run(String... args) throws Exception {
        server.start();
        System.out.println("Serveur Socket.IO démarré!");
    }

    @PreDestroy
    public void stopSocketIOServer() {
        if (server != null) {
            server.stop();
            System.out.println("Serveur Socket.IO arrêté!");
        }
    }
}