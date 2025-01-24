package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "users")
public class MyUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;


    private String username;
    private String password;
    private String email;
    private String name;
    private String surname;
    private String phonenum;
    private String role;

    private String verificationCode;
    private LocalDateTime verificationCodeExpiresAt;
    private boolean enabled; //potvrdio email verifikacijskim kodom
    private boolean validated; //potvdio ga admin


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserProject> appliedProjects = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // Omogućava pravilnu serijalizaciju
    private List<Note> notes = new ArrayList<>();




}

