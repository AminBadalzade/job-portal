package com.amin.jobportal.repository;

import com.amin.jobportal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> getUserByEmail(String email);

    Boolean existsUserByEmail(String email);
}

