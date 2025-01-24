package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.demo.model.MyUser;
import com.example.demo.model.Project;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.UserProjectRepository;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;


class UserProjectServiceTest {

    @Mock
    private UserProjectRepository userProjectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private UserProjectService userProjectService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void applyToProjectSuccess() {
        MyUser testUser = new MyUser();
        testUser = new MyUser();
        testUser.setUsername("TempAcc2");
        testUser.setPassword("test638");
        testUser.setEmail("dogiv33450@citdaca.com");
        testUser.setName("Probni2");
        testUser.setSurname("Account2");
        testUser.setPhonenum("24433355");
        testUser.setRole("volonter");
        testUser.setId(1L);
        userRepository.save(testUser);

        Project testProject = new Project();
        testProject.setId(1L);
        testProject.setImeProjekta("Probni projekt");
        testProject.setBrojLjudi(3);
        testProject.setJeLiHitno(false);
        testProject.setDatumPoc(LocalDate.of(2025, 3, 8));
        testProject.setDatumKraj(LocalDate.of(2025, 4, 13));
        projectRepository.save(testProject);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        boolean result = userProjectService.applyToProject(1L, 1L);
        assertTrue(result);
    }
}
