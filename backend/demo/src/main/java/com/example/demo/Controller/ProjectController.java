package com.example.demo.Controller;

import com.example.demo.model.Project;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;
    @Autowired
    private ProjectRepository projectRepository;

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

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Project>> getProjectsByOwner(@PathVariable String ownerId) {
        List<Project> projects = projectRepository.findByOwnerId(ownerId);
        return ResponseEntity.ok(projects);
    }

}
