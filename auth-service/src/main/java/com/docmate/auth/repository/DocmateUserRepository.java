package com.docmate.auth.repository;

import com.docmate.auth.entity.DocmateUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for storing and retrieving users.
 */
public interface DocmateUserRepository extends JpaRepository<DocmateUser, Long> {
    Optional<DocmateUser> findByEmail(String email);
}