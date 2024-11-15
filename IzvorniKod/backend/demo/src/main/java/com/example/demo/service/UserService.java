package com.example.demo.service;


import com.example.demo.dto.UserRegistrationDto;
import com.example.demo.model.MyUser;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean authenticate(String username, String password) {
        Optional<MyUser> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            MyUser user = userOpt.get();
            return passwordEncoder.matches(password, user.getPassword());
        }
        return false;
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

