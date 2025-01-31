package com.example.demo.Controller;
import java.util.Optional;

import com.example.demo.dto.ProjectUpdateDTO;
import com.example.demo.model.UserProject;
import com.example.demo.model.MyUser;
import com.example.demo.dto.ApplicationDto;
import com.example.demo.model.Project;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.UserRepository;

import com.example.demo.service.ProjectService;
import com.example.demo.service.UserProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.lang.Long;
import java.util.Map;
import java.util.Map;
import java.util.Optional;

//Controller zahtjeva za projekte i prijave na projekte

@RestController
@CrossOrigin
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserProjectService userProjectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ResponseEntity<Project> kreirajProjekt(@RequestBody Project project) {
        Project savedProject = projectService.kreirajProjekt(project);
        return ResponseEntity.ok(savedProject);
    }

    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects() {
        List<Project> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }

    //dohvati podatke o projektu preko id-a
    @GetMapping("/{id}")
    public ResponseEntity<Project> getProject(@PathVariable Long id) {
        Optional<Project> project = projectRepository.findById(id);
        if (project.isPresent()) {
            return ResponseEntity.ok(project.get());
        } else {
            return null;
        }
    }

    //izbriši projekt preko id-a
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable Long id) {
        Optional<Project> project = projectRepository.findById(id);
        if (project.isPresent()) {
            projectRepository.delete(project.get());
            return ResponseEntity.ok(Map.of("message", "Projekt uspješno obrisan."));
        } else {
            return ResponseEntity.status(404).body("Projekt nije pronađen.");
        }
    }

    //dohvati projekte koje je objavio user username
    @GetMapping("/owner/{username}")
    public ResponseEntity<List<Map<String, Object>>> getProjectsByOwner(
            @PathVariable String username
    ){
        List<Map<String, Object>> projectsWithStatus = projectRepository.findByOwnerId(username).stream()
                .map(project -> Map.of(
                        "project", project,
                        "status", "undefined"
                ))
                .toList();
        return ResponseEntity.ok(projectsWithStatus);
    }

    //dohvati projekte na koje je volonter prijavljen / bio je prijavljen
    @GetMapping("/owner/{username}/withstatus")
    public ResponseEntity<List<Map<String, Object>>> getVolunteerProjects(@PathVariable String username) {
        List<Map<String, Object>> projectStatuses = userProjectService.getProjectsWithStatus(username).entrySet()
                .stream()
                .map(entry -> Map.of(
                        "project", entry.getKey(),
                        "status", entry.getValue()))
                .toList();
        return ResponseEntity.ok(projectStatuses);
    }

    //prijavi se na projekt - stvori prijavu
    @PostMapping("/{projectId}/apply")
    public ResponseEntity<String> applyToProject(
            @PathVariable Long projectId,
            @RequestBody Map<String, Object> requestBody)
    {

        Long userId = Long.valueOf(requestBody.get("userId").toString());

        boolean success = userProjectService.applyToProject(userId, projectId);
        if (success) {
            return ResponseEntity.ok("Successfully applied to the project!");
        } else {
            return ResponseEntity.badRequest().body("Failed to apply to the project.");
        }
    }

    //dohvati sve prijave projekta preko projektIda
    @GetMapping("/{projectId}/applications")
    public ResponseEntity<List<ApplicationDto>> getAllApplications(
            @PathVariable Long projectId
    ) {
        List<UserProject> applications = userProjectService.getAllApplications(projectId);
        System.out.println("Saljem prijave frontendu: " + applications);
        ArrayList<ApplicationDto> app = new ArrayList<ApplicationDto>();
        for(UserProject a : applications){
            app.add(userProjectService.getApplicationDto(a));
        }

        return ResponseEntity.ok(app);
    }

    //organizator odobrava prijavu volontera na projekt s projektId
    @PostMapping("/{projectId}/{applicationId}/accept")
    public ResponseEntity<String> acceptApplication(
            @PathVariable Long projectId,
            @PathVariable Long applicationId
    ) {
        Boolean success = userProjectService.setApplicationStatus(applicationId, "accepted");
        if (success) {
            return ResponseEntity.ok("Application successfully accepted!");
        } else {
            return ResponseEntity.badRequest().body("Failed to accept the application.");
        }
    }

    //uredi podatke o projektu
    @PutMapping
    public ResponseEntity<Project> updateProject(@RequestBody ProjectUpdateDTO projectDTO) {
        Project updatedProject = projectService.updateProject(projectDTO);
        return ResponseEntity.ok(updatedProject);
    }

    //organizator odbija prijavu na projekt
    @PostMapping("/{projectId}/{applicationId}/decline")
    public ResponseEntity<String> declineApplication(
            @PathVariable Long projectId,
            @PathVariable Long applicationId
    ) {
        Boolean success = userProjectService.setApplicationStatus(applicationId, "declined");
        if (success) {
            return ResponseEntity.ok("Application successfully declined!");
        } else {
            return ResponseEntity.badRequest().body("Failed to decline the application.");
        }
    }



}
