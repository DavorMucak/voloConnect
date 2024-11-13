package com.example.demo.controller;

import com.example.demo.dto.UserRegistrationDto;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@RestController
@CrossOrigin
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final UserRepository userRepository;

    @Autowired
    public AuthController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;

    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRegistrationDto userDto) {
        String result = userService.register(userDto);

        if (result.equals("Uspješna registracija!")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    // Testna metoda za spremanje korisnika u bazu
    @PostMapping("/testRegister")
    public ResponseEntity<String> testRegister() {
        // Testni podaci za korisnika
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("password");
        user.setEmail("test@test.com");
        user.setName("Test");
        user.setSurname("User");
        user.setPhonenum("123456789");
        user.setRole("volonter");

        // Spremi korisnika u bazu
        userRepository.save(user);

        // Vraćanje odgovora nakon uspješnog spremanja
        return ResponseEntity.ok("User saved successfully!");
    }
}
