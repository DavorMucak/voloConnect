package com.example.demo.service;

import com.example.demo.dto.ProjectUpdateDTO;
import com.example.demo.model.MyUser;
import com.example.demo.model.Project;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    private final UserRepository userRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    public Project kreirajProjekt(Project project) {
        System.out.println("Saving project to database...");
        return projectRepository.save(project);
    }


    public Project updateProject(ProjectUpdateDTO projectDTO) {
        // Pronađi projekt u bazi podataka prema imenu
        Optional<Project> optionalProject = projectRepository.findByImeProjekta(projectDTO.getImeProjekta());

        if (optionalProject.isEmpty()) {
            throw new RuntimeException("Projekt s imenom " + projectDTO.getImeProjekta() + " ne postoji!");
        }

        // Ažuriraj projekt
        Project project = optionalProject.get();
        project.setOpisProjekta(projectDTO.getOpisProjekta());
        project.setBrojLjudi(projectDTO.getBrojLjudi());
        project.setDatumPoc(LocalDate.parse(projectDTO.getDatumPoc(), DateTimeFormatter.ISO_DATE));
        project.setDatumKraj(LocalDate.parse(projectDTO.getDatumKraj(), DateTimeFormatter.ISO_DATE));
        project.setJeLiHitno(projectDTO.isJeLiHitno());
        project.setOwnerId(projectDTO.getOwnerId());

        // Spremi promjene u bazu
        return projectRepository.save(project);
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

}
