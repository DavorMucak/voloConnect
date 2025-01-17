package com.example.demo.service;


import com.example.demo.dto.UserLoginDto;
import com.example.demo.dto.UserRegistrationDto;
import com.example.demo.dto.VerifyUserDto;
import com.example.demo.model.MyUser;
import com.example.demo.repository.UserRepository;
import jakarta.mail.MessagingException;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class UserService {

    @Autowired
    private JwtService jwtService;

    @Autowired
    AuthenticationManager authM;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.authenticationManager = authenticationManager;
    }

    public String authenticate(UserLoginDto userLoginDto) {
        Authentication authentication = authM.authenticate(new UsernamePasswordAuthenticationToken(userLoginDto.getUsername(), userLoginDto.getPassword()));
        Optional<MyUser> userOpt = userRepository.findByUsername(userLoginDto.getUsername());
        if (userOpt.isPresent()) {
            if (authentication.isAuthenticated()){
                return jwtService.generateToken(userOpt.get().getUsername(), userOpt.get().getRole());
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
        if(userDto.getPassword() != null)
            user.setPassword(passwordEncoder.encode(userDto.getPassword())); // šifriranje lozinke
        user.setEmail(userDto.getEmail());
        if(userDto.getPassword() != null)
            user.setName(userDto.getName());
        if(userDto.getPassword() != null)
            user.setSurname(userDto.getSurname());
        if(userDto.getPassword() != null)
            user.setPhonenum(userDto.getPhonenum());
        user.setRole(userDto.getRole());

        user.setVerificationCode(generateVerificationCode());
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
        user.setEnabled(false);
        user.setValidated(false);
        sendVerificationEmail(user);

        System.out.println("Saving user to database...");
        userRepository.save(user);
        return user.getVerificationCode();
    }


    public void verifyUser(VerifyUserDto input) {
        Optional<MyUser> optionalUser = userRepository.findByEmail(input.getEmail());
        if (optionalUser.isPresent()) {
            MyUser user = optionalUser.get();
            if (user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Verification code has expired");
            }
            if (user.getVerificationCode().equals(input.getVerificationCode())) {
                user.setEnabled(true);
                user.setVerificationCode(null);
                user.setVerificationCodeExpiresAt(null);
                userRepository.save(user);
            } else {
                throw new RuntimeException("Invalid verification code");
            }
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public void resendVerificationCode(String email) {
        Optional<MyUser> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            MyUser user = optionalUser.get();
            if (user.isEnabled()) {
                throw new RuntimeException("Account is already verified");
            }
            user.setVerificationCode(generateVerificationCode());
            user.setVerificationCodeExpiresAt(LocalDateTime.now().plusHours(1));
            sendVerificationEmail(user);
            userRepository.save(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    private void sendVerificationEmail(MyUser user) { //TODO: Update with company logo
        String subject = "Account Verification";
        String verificationCode = "VERIFICATION CODE " + user.getVerificationCode();
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Welcome to our app!</h2>"
                + "<p style=\"font-size: 16px;\">Please enter the verification code below to continue:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Verification Code:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + verificationCode + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            // Handle email sending exception
            e.printStackTrace();
        }
    }
    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }

    //mozda nije najelegantnije rjesenje ali je najbolje kojeg sam se mogla sjetiti sad
    public List<MyUser> getUnvalidatedUsers() {
        List<MyUser> allUsers = userRepository.findAll();
        List<MyUser> unvalidatedUsers = new ArrayList<>();
        for(MyUser user : allUsers) {
            if(!user.isValidated())
                unvalidatedUsers.add(user);
        }
        return unvalidatedUsers;
    }
}

