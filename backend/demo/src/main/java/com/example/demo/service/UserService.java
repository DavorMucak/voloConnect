package com.example.demo.service;


import com.example.demo.dto.UserLoginDto;
import com.example.demo.dto.UserRegistrationDto;
import com.example.demo.model.MyUser;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private JWTService jwtService;

    @Autowired
    AuthenticationManager authM;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String authenticate(UserLoginDto userLoginDto) {
        Authentication authentication = authM.authenticate(new UsernamePasswordAuthenticationToken(userLoginDto.getUsername(), userLoginDto.getPassword()));
        Optional<MyUser> userOpt = userRepository.findByUsername(userLoginDto.getUsername());
        if (userOpt.isPresent()) {
            if (authentication.isAuthenticated()){
                return jwtService.generateToken(userOpt.get().getUsername());
            } else {
                return "fail";
            }
        }
        return "fail";
    }



    public String register(UserRegistrationDto userDto) {
        if (userRepository.existsByUsername(userDto.getUsername())) {
            return "Korisničko ime je već zauzeto.";
        }

        if (userRepository.existsByEmail(userDto.getEmail())) {
            return "Email je već registriran.";
        }

        MyUser user = new MyUser();
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword())); // šifriranje lozinke
        user.setEmail(userDto.getEmail());
        user.setName(userDto.getName());
        user.setSurname(userDto.getSurname());
        user.setPhonenum(userDto.getPhonenum());
        user.setRole(userDto.getRole());

        System.out.println("Saving user to database...");
        userRepository.save(user);
        return "Uspješna registracija!";
    }
}

