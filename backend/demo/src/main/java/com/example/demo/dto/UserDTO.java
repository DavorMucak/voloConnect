package com.example.demo.dto;

import com.example.demo.model.MyUser;

//dto = data to object
//pomocne klase za formatiranje i slanje podataka na front

public class UserDTO {
    private Long id; // Pretpostavljam da imaš ID
    private String username;
    private String email;
    private String name;
    private String surname;
    private String phonenum;
    private String role; // Ili kako god je u tvojoj klasi definiran atribut uloge

    // Konstruktor koji prima entitet `MyUser`
    public UserDTO(MyUser user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.name = user.getName();
        this.surname = user.getSurname();
        this.phonenum = user.getPhonenum();
        this.role = user.getRole();
    }

    // Getteri i setteri
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPhonenum() {
        return phonenum;
    }

    public void setPhonenum(String phonenum) {
        this.phonenum = phonenum;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

