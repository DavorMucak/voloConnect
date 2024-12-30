package com.example.demo.service;

import com.example.demo.dto.UserLoginDto;
import com.example.demo.dto.UserRegistrationDto;
import com.example.demo.model.CustomOAuth2User;
import com.example.demo.model.MyUser;
import com.example.demo.model.UserPrincipal;
import com.example.demo.model.MyUser;
import com.example.demo.repository.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;


import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.provider.google.issuer-uri}")
    private String issuer;

    public OAuth2User verifyOAuth2Token(String token) throws IOException, GeneralSecurityException {
        // Inicijaliziraj verifyer za Google ID token
        GsonFactory jsonFactory = GsonFactory.getDefaultInstance();
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), jsonFactory)
                .setAudience(Collections.singletonList(clientId))
                .setIssuer(issuer)
                .build();

        GoogleIdToken idToken = verifier.verify(token);

        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();

            // Extract email and username
            String email = payload.getEmail();
            String username = (String) payload.getSubject();

            // Map selected attributes
            Map<String, Object> attributes = Map.of(
                    "email", email,
                    "username", username,
                    "name", payload.get("name")
            );

            Optional<MyUser> user = userRepository.findByEmail(email);

            // user nije pronaden -> baci gresku (ili automatski registriraj korisnika
            if (user.isEmpty()) {
                throw new IllegalStateException("User not found. Registration required.");
            }

            UserPrincipal userPrincipal = new UserPrincipal(user);

            return new CustomOAuth2User(userPrincipal, attributes);

        } else {
            throw new IllegalArgumentException("Invalid or expired Google ID token.");
        }
    }
}

