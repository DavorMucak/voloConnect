package com.example.demo;
import com.example.demo.dto.UserRegistrationDto;
import com.example.demo.model.MyUser;
import com.example.demo.service.UserService;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp(){
        MyUser testUser = new MyUser();
        testUser.setUsername("ivek");
        testUser.setPassword("Admintest123");
        testUser.setEmail("rebovo6659@dfesc.com");
        testUser.setName("Admin");
        testUser.setSurname("Test");
        testUser.setPhonenum("5334566");
        testUser.setRole("admin");
        testUser.setValidated(true);
        userRepository.save(testUser);
    }

    @Test
    void usernameTaken() {

        UserRegistrationDto URD = new UserRegistrationDto();
        URD.setUsername("ivek");
        URD.setPassword("testtest");
        URD.setEmail("lirapiw704@fanicle.com");
        URD.setName("Ivan");
        URD.setSurname("Peric");
        URD.setPhonenum("123456789");
        URD.setRole("volonter");
        assertEquals("Korisničko ime je već zauzeto.", userService.register(URD));
    }

    @Test
    void ApproveAdminThatIsAlreadyApproved() {
        MyUser testUser = new MyUser();
        testUser.setUsername("TestAdmin");
        testUser.setPassword("admintest");
        testUser.setEmail("rebovo6659@dfesc.com");
        testUser.setName("Admin");
        testUser.setSurname("Test");
        testUser.setPhonenum("5334566");
        testUser.setRole("admin");
        testUser.setValidated(true);
        userRepository.save(testUser);

        assertThrows(RuntimeException.class, () -> {
            userService.approveAdmin("TestAdmin");
        });
    }


}
