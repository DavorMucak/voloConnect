package com.example.demo.repository;

import com.example.demo.model.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<MyUser, Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<MyUser> findByUsername(String username);
    Optional<MyUser> findByEmail(String email);
}
