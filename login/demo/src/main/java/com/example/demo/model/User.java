package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter

    private Long id;


    @Getter @Setter
    private String username;
    @Getter @Setter
    private String password;
    @Getter @Setter
    private String email;
    @Getter @Setter
    private String name;
    @Getter @Setter
    private String surname;
    @Getter @Setter
    private String phonenum;
    @Getter @Setter
    private String role;


}
