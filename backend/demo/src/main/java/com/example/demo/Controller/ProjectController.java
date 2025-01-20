package com.example.demo.Controller;

import com.example.demo.model.Project;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.service.ProjectService;
import com.example.demo.service.UserProjectService;
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

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Project>> getProjectsByOwner(@PathVariable String ownerId) {
        List<Project> projects = projectRepository.findByOwnerId(ownerId);
        return ResponseEntity.ok(projects);
    }

    @PostMapping("/{projectId}/apply")
    public ResponseEntity<String> applyToProject(
            @PathVariable Long projectId,
            @RequestParam Long userId)
    {

        boolean success = userProjectService.applyToProject(userId, projectId);
        if (success) {
            return ResponseEntity.ok("Successfully applied to the project!");
        } else {
            return ResponseEntity.badRequest().body("Failed to apply to the project.");
        }
    }

}
