package com.example.demo.service;

import com.example.demo.model.MyUser;
import com.example.demo.model.Project;
import com.example.demo.model.UserProject;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.UserProjectRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserProjectService {

    @Autowired
    private UserProjectRepository userProjectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    public boolean applyToProject(Long userId, Long projectId) {
        Optional<MyUser> user = userRepository.findById(userId);
        Optional<Project> project = projectRepository.findById(projectId);

        if (user.isPresent() && project.isPresent()) {
            UserProject userProject = new UserProject();
            userProject.setUser(user.get());
            userProject.setProject(project.get());
            userProjectRepository.save(userProject);
            return true;
        }
        return false;
    }
}

