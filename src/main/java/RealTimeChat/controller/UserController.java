package RealTimeChat.controller;

import RealTimeChat.model.User;
import RealTimeChat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService ;


    @PostMapping
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        try {
            User registeredUser = userService.registerUser(user);
            return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        Optional<User> user = userService.findUserByUsername(username);
        return user.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }


    @GetMapping("/online")
    public ResponseEntity<List<User>> getOnlineUsers() {
        List<User> onlineUsers = userService.findAllOnlineUsers();
        return new ResponseEntity<>(onlineUsers, HttpStatus.OK);
    }


    @PutMapping("/{id}/status")
    public ResponseEntity<User> updateUserOnlineStatus(
            @PathVariable Integer id,
            @RequestBody Map<String, Boolean> status) {

        Boolean isOnline = status.get("isOnline");
        if (isOnline == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        try {
            User updatedUser = userService.updateUserOnlineStatus(id, isOnline);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
}