// ************** Attention JPA fournit déjà des méthodes native pour reucperer des données on a pas
// besoin de les réimplémenter dans le repository pour info une liste non exaustive : **************

//save(User user) : Crée ou met à jour un utilisateur
//saveAll(Iterable<User> users) : Sauvegarde plusieurs utilisateurs
//findById(Integer id) : Trouve un utilisateur par ID (retourne un Optional)
//existsById(Integer id) : Vérifie si un ID existe
//findAll() : Récupère tous les utilisateurs
//count() : Compte le nombre total d'utilisateurs
//deleteById(Integer id) : Supprime un utilisateur par ID
//delete(User user) : Supprime un utilisateur
//deleteAll() : Supprime tous les utilisateurs

package RealTimeChat.repository;

import RealTimeChat.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<User> findByIsOnlineTrue();
}