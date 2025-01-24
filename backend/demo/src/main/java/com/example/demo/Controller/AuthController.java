package com.example.demo.Controller;

import com.example.demo.dto.UserLoginDto;
import com.example.demo.dto.UserRegistrationDto;
import com.example.demo.dto.VerifyUserDto;
import com.example.demo.model.MyUser;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.CustomOAuth2UserService;
import com.example.demo.service.JwtService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

//Controller prima requestove na zadanu adresu i prosljeduje dalje backendu na obradu
//direktno komunicira s frontendom
//Controller za autentikacijske url-ove

@RestController
@CrossOrigin
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.provider.google.issuer-uri}")
    private String issuer;

    @Autowired
    public AuthController(UserService userService, CustomOAuth2UserService customOAuth2UserService, UserRepository userRepository, JwtService jwtService) {
        this.userService = userService;
        this.customOAuth2UserService = customOAuth2UserService;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    //registracija
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRegistrationDto userDto) {
        String result = userService.register(userDto);

        if (result.equals("Korisničko ime je već zauzeto.") || result.equals("Email je već registriran.")) {
            return ResponseEntity.badRequest().body(result);
        } else {
            return ResponseEntity.ok(result);
        }
    }

    //login
    @PostMapping("/login")
    public String login(@RequestBody UserLoginDto loginDto) {
        System.out.println("Login endpoint called with: " + loginDto);
        return userService.authenticate(loginDto);
    }

    //potvrda verifikacijskog koda
    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestBody VerifyUserDto verifyUserDto) {
        try {
            userService.verifyUser(verifyUserDto);
            return ResponseEntity.ok("Account verified successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //ponovno slanje verifikacijskog koda
    @PostMapping("/resend")
    public ResponseEntity<?> resendVerificationCode(@RequestParam String email) {
        try {
            userService.resendVerificationCode(email);
            return ResponseEntity.ok("Verification code sent");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //brisanje user računa
    @DeleteMapping("/delete-account/{username}")
    public ResponseEntity<?> deleteAccount(@PathVariable String username) {
        try {
            Optional<MyUser> user = userRepository.findByUsername(username);
            if (user.isPresent()) {
                userRepository.delete(user.get());
                return ResponseEntity.ok().body(Map.of("message", "Korisnički račun za '" + username + "' uspješno obrisan."));
            } else {
                return ResponseEntity.status(404).body("Korisnik s korisničkim imenom '" + username + "' nije pronađen.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Došlo je do greške pri brisanju korisničkog računa.");
        }
    }

    //google-login (+ automatska registracija impolementirana)
    @PostMapping("/google-login")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> payload) {
        String idToken = payload.get("idToken");
        if (idToken == null || idToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid ID token");
        }
        String selectedRole = payload.get("role");
        if(selectedRole == null) selectedRole = "";
        try {
            OAuth2User oAuth2User = customOAuth2UserService.verifyOAuth2Token(idToken, selectedRole);
            if (oAuth2User == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Google ID token");
            }

            String username = oAuth2User.getAttribute("username");
            String role = oAuth2User.getAttribute("role");
            String userId = Long.toString(oAuth2User.getAttribute("userId"));

            String jwt = jwtService.generateToken(username, role, userId);

            //Saznaj ima li user odreden role, ako ima proslijedi ga na frontend
            return ResponseEntity.ok(Map.of(
                    "token", jwt
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Google authentication");
        }
    }
}
