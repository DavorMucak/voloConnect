package com.example.demo.Controller;

import com.example.demo.dto.UserDTO;
import com.example.demo.model.MyUser;
import com.example.demo.model.UserPrincipal;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ProjectService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;


    @Autowired
    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }


    @GetMapping("/approve-admins")
    public ResponseEntity<List<MyUser>> getUnvalidatedAdmins() {
        List<MyUser> users = userService.getUnvalidatedAdmins();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/approve-admins/{username}")
    public ResponseEntity<String> approveAdmin(@PathVariable String username) {
        Optional<MyUser> user = userRepository.findByUsername(username);
        if (!user.isPresent()) {
            return ResponseEntity.status(404).body("Korisnik s korisničkim imenom " + username + " nije pronađen.");
        }
        userService.approveAdmin(username);
        return ResponseEntity.status(200).body("Admin " + username + " odobren!");
    }

    @DeleteMapping("/approve-admins/{username}")
    public ResponseEntity<String> disapproveAdmin(@PathVariable String username){
        Optional<MyUser> user = userRepository.findByUsername(username);
        if (!user.isPresent()) {
            return ResponseEntity.status(404).body("Korisnik s korisničkim imenom " + username + " nije pronađen.");
        }
        userService.disapproveAdmin(username);
        return ResponseEntity.status(200).body("Admin zahtjev " + username + " izbrisan!");
    }

    // Endpoint za dohvaćanje korisnika prema korisničkom imenu
    @GetMapping("/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        Optional<MyUser> user = userRepository.findByUsername(username);

        if (user.isPresent()) {
            // Vraćamo samo DTO umjesto kompletnog objekta
            return ResponseEntity.ok(new UserDTO(user.get()));
        } else {
            return ResponseEntity.status(404).body("Korisnik s korisničkim imenom '" + username + "' nije pronađen.");
        }
    }

}
