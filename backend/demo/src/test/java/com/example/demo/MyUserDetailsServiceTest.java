package com.example.demo;

import com.example.demo.model.MyUser;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.MyUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class MyUserDetailsServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @Test
    void LoadUserByUsernameUserFound() {
        MyUser testUser;
        testUser = new MyUser();
        testUser.setUsername("TempAcc");
        testUser.setPassword("Probni123");
        testUser.setEmail("xociwo9539@citdaca.com");
        testUser.setName("Probni");
        testUser.setSurname("Account");
        testUser.setPhonenum("000000000");
        testUser.setRole("volonter");
        userRepository.save(testUser);
        UserDetails userDetails = myUserDetailsService.loadUserByUsername("TempAcc");
        assertEquals("TempAcc", userDetails.getUsername());
    }
}