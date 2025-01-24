package com.example.demo.dto;
import com.example.demo.model.UserProject;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ApplicationDto {

    private Long id;
    private Long userId;
    private String userName;
    private Long projectId;
    private String projectName;
    private LocalDateTime applicationDate;
    private String status;

    public ApplicationDto(Long id, Long userId, String userName, Long projectId, String projectName, LocalDateTime applicationDate, String status) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.projectId = projectId;
        this.projectName = projectName;
        this.applicationDate = applicationDate;
        this.status = status;
    }

    public ApplicationDto(UserProject userProject){
        this.id = userProject.getId();
        this.userId = userProject.getUser().getId();
        this.projectId = userProject.getProject().getId();
        this.applicationDate = userProject.getApplicationDate();
        this.status = userProject.getStatus();
    }
}

