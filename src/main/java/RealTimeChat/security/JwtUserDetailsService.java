package RealTimeChat.security;

import RealTimeChat.model.User;
import RealTimeChat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            Integer id = Integer.parseInt(username);
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec l'ID: " + id));

            return new org.springframework.security.core.userdetails.User(
                    id.toString(),
                    user.getPassword(),
                    new ArrayList<>());
        } catch (NumberFormatException e) {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé: " + username));

            return new org.springframework.security.core.userdetails.User(
                    user.getId().toString(), // Utiliser l'ID comme "username" pour le UserDetails
                    user.getPassword(),
                    new ArrayList<>());
        }
    }
}