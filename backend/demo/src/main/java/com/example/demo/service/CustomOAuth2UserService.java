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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.provider.google.issuer-uri}")
    private String issuer;

    public OAuth2User verifyOAuth2Token(String token, String selectedRole) throws IOException, GeneralSecurityException {
        logger.info("Pokrenuta funkcija verifyOAuth2Token");
        logger.info(token);
        // Inicijaliziraj verifyer za Google ID token
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(clientId))
                .setIssuer(issuer)
                .build();

        GoogleIdToken idToken = verifier.verify(token);
        logger.info("Generiran verifier, verificiram Google ID token");
        //PROBLEM JE STO IDTOKEN BUDE NULL!!!
        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();

            // Izvuci podatke o useru iz tokena
            String email = payload.getEmail();
            if(!payload.getEmailVerified()){
                logger.info("Email nije verificiran od strane Googlea.");
                throw new IllegalArgumentException("Invalid email.");
            }
            logger.info("Email verificiran od strane Googlea.");
            Object nameObject = "name";
            String name = (String) payload.get(nameObject);
            logger.info("ucitan email: " + email + ", ucitano ime: " + name + ".");

            Optional<MyUser> user = userRepository.findByEmail(email);

            // user nije pronaden -> baci gresku (ili automatski registriraj korisnika)
            if (user.isEmpty()) {
                /* //BACAM GRESKU AKO USER NIJE PRONADEN
                logger.info("User not found error");
                throw new IllegalStateException("User not found. Registration required.");
                 */

                //AKO USER NIJE PRONADEN REGISTRIRAJ GA
                MyUser newUser = new MyUser();
                newUser.setEmail(email);
                Object givenName = "given_name";
                String firstName = (String) payload.get(givenName);
                newUser.setName(firstName);
                Object familyName = "family_name";
                String surname = (String) payload.get(familyName);
                newUser.setSurname(surname);
                //SKUZI KAKO DA ODREDIS ROLE NOVOG USERA
                newUser.setRole(selectedRole);
                //kod prijave googleom nije potrebno slati verifikacijski mail?
                newUser.setEnabled(true);
                newUser.setValidated(true);
                System.out.println("Saving user to database...");
                userRepository.save(newUser);
                logger.info("Novi korisnik registriran u bazu podataka.");
                Optional<MyUser> userSetup = userRepository.findByEmail(email);
                if (userSetup.isPresent()) {
                    MyUser userTemp = userSetup.get();
                    Long id = userTemp.getId();
                    while(userRepository.existsByUsername(firstName + surname + id)) {
                        id = id * 2;
                    }
                    userTemp.setUsername(firstName + surname + id);
                    userRepository.save(userTemp);
                }
                user = userRepository.findByEmail(email);
            }

            UserPrincipal userPrincipal = new UserPrincipal(user);
            logger.info("Token uspjesno verificiran, stvaram novi CustomOAuth2User.");

            Map<String, Object> attributes = Map.of(
                    "email", email,
                    "name", name,
                    "role", user.get().getRole(),
                    "userId", user.get().getId(),
                    "username", user.get().getUsername()
            );

            return new CustomOAuth2User(userPrincipal, attributes);

        } else {
            logger.info("Google ID Token nije prosao verifikaciju (idToken == null).");
            throw new IllegalArgumentException("Invalid or expired Google ID token.");
        }
    }
}

