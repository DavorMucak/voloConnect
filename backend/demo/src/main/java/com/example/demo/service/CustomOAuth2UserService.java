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

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public OAuth2User verifyOAuth2Token(String token) throws IOException, GeneralSecurityException {
        //Inicijaliziram GoogleIDToken verifikator
        GsonFactory jsonFactory = GsonFactory.getDefaultInstance();
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), jsonFactory)
                .setAudience(Collections.singletonList("368455952414-n0qaeppdv3gu4qpofn5f6jkc0gu4l19u.apps.googleusercontent.com"))
                .setIssuer("https//accounts.google.com")
                .build();

        GoogleIdToken idToken = verifier.verify(token);

        if (idToken == null) {

            GoogleIdToken.Payload payload = idToken.getPayload();

            String email = payload.getEmail();
            String username = (String) payload.getSubject();

            Map<String, Object> attributes = payload;


            Optional<MyUser> user = userRepository.findByEmail(email);
            UserPrincipal userPrincipal = new UserPrincipal(user);

            return new CustomOAuth2User(userPrincipal, attributes);

        } else {
            System.out.println("Neuspjela verifikacija");
            return null;
        }
    }
}
