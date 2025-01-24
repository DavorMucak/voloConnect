package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

//dto = data to object
//pomocne klase za formatiranje i slanje podataka na front

@Getter
@Setter
public class VerifyUserDto {
    private String email;
    private String verificationCode;
}
