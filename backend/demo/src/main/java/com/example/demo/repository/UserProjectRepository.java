package com.example.demo.repository;

import com.example.demo.model.UserProject;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

//repository za prijave na projekte (applications)

public interface UserProjectRepository extends JpaRepository<UserProject, Long> {
    List<UserProject> findByUserId(Long userId);
    List<UserProject> findByProjectId(Long projectId);
    Optional<UserProject> findById(Long applicationId);
}

