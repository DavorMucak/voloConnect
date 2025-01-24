package com.example.demo.service;

import com.example.demo.model.MyUser;
import com.example.demo.model.Project;
import com.example.demo.model.UserProject;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.UserProjectRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.dto.ApplicationDto;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.List;

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
            userProject.setStatus("waiting");
            userProjectRepository.save(userProject);
            return true;
        }
        return false;
    }

    public List<UserProject> getAllApplications(Long projectId){
        List<UserProject> applications = userProjectRepository.findByProjectId(projectId);
        return applications;
    }

    public boolean setApplicationStatus(Long applicationId, String newStatus){
        Optional<UserProject> application = userProjectRepository.findById(applicationId);
        if(application.isPresent()){
            UserProject appl = application.get();
            appl.setStatus(newStatus);
            userProjectRepository.save(appl);
            return true;
        } else {
            System.out.println("Application not found");
            return false;
        }
    }

    public Map<Project,String> getProjectsWithStatus (String username){
        HashMap<Project, String> projects = new HashMap<Project, String>();
        Optional<MyUser> userOptional = userRepository.findByUsername(username);
        if(!userOptional.isPresent()){
            throw new RuntimeException("User not found");
        }
        MyUser user = userOptional.get();
        List<UserProject> applications = userProjectRepository.findByUserId(user.getId());
        for(UserProject application : applications){
            projects.put(application.getProject(), application.getStatus());
        }
        return projects;
    }

    public ApplicationDto getApplicationDto(UserProject appl) {
        MyUser tempUser = appl.getUser();
        Long userId = null;
        String userName = "";
        if(tempUser != null){
            userId = tempUser.getId();
            userName = tempUser.getUsername();
        }
        Long projectId = null;
        String projectName = "";
        Project tempProject = appl.getProject();
        if(tempProject != null){
            projectId = tempProject.getId();
            projectName = tempProject.getImeProjekta();
        }
        return new ApplicationDto(appl.getId(), userId, userName, projectId, projectName, appl.getApplicationDate(), appl.getStatus());
    }


}

