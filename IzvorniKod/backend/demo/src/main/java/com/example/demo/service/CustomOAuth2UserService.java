package com.example.demo.service;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Optional;

import com.example.demo.model.MyUser;
import com.example.demo.repository.UserRepository;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private UserRepository userRepository;

    @Autowired
    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        //izvuci atribute iz Google responsea
        Map<String, Object> attributes = oAuth2User.getAttributes();
        if (attributes == null || attributes.isEmpty()) {
            throw new RuntimeException("Neuspjelo učitavanje atributa iz Google odgovora.");
        }

        String email = (String) attributes.get("email");
        //String name = (String) attributes.get("name");

        Optional<MyUser> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Korisnik nije registriran. Preusmjeravam na registraciju");
        }

        return oAuth2User;
    }
}

