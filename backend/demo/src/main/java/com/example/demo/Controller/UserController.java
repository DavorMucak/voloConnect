package com.example.demo.Controller;

import com.example.demo.dto.UserDTO;
import com.example.demo.model.MyUser;
import com.example.demo.model.UserPrincipal;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ProjectService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;
import java.util.List;

//Controller za podatke o userima

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

    //postojeci, aktivni admin dohvaca zahtjeve za registraciju novih admina
    @GetMapping("/approve-admins")
    public ResponseEntity<List<MyUser>> getUnvalidatedAdmins() {
        List<MyUser> users = userService.getUnvalidatedAdmins();
        return ResponseEntity.ok(users);
    }

    //uredi podatke o useru
    @PutMapping("/{username}")
    public ResponseEntity<?> updateUser(
            @PathVariable String username,
            @RequestBody Map<String, String> updatedDetails) {
        try {
            // Pronađi korisnika po korisničkom imenu
            Optional<MyUser> optionalUser = userRepository.findByUsername(username);

            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Korisnik nije pronađen.");
            }

            MyUser user = optionalUser.get();

            // Ažuriraj podatke korisnika na temelju dolaznih podataka
            if (updatedDetails.containsKey("email")) {
                user.setEmail(updatedDetails.get("email"));
            }
            if (updatedDetails.containsKey("name")) {
                user.setName(updatedDetails.get("name"));
            }
            if (updatedDetails.containsKey("surname")) {
                user.setSurname(updatedDetails.get("surname"));
            }
            if (updatedDetails.containsKey("phonenum")) {
                user.setPhonenum(updatedDetails.get("phonenum"));
            }

            // Spremi promjene u bazu podataka
            userRepository.save(user);

            return ResponseEntity.ok("Podaci korisnika uspješno ažurirani.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Dogodila se greška prilikom ažuriranja korisnika.");
        }
    }

    //admin odobrava novog admina s usernameom username
    @PostMapping("/approve-admins/{username}")
    public ResponseEntity<String> approveAdmin(@PathVariable String username) {
        Optional<MyUser> user = userRepository.findByUsername(username);
        if (!user.isPresent()) {
            return ResponseEntity.status(404).body("Korisnik s korisničkim imenom " + username + " nije pronađen.");
        }
        userService.approveAdmin(username);
        return ResponseEntity.status(200).body("Admin " + username + " odobren!");
    }

    //admin briše zahtjev za registracijom novog admina s usernameom username
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
