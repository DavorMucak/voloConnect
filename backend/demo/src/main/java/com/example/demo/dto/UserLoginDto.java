package com.example.demo.dto;

//dto = data to object
//pomocne klase za formatiranje i slanje podataka na front

public class UserLoginDto {
    private String username;
    private String password;

    // Getters and setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

