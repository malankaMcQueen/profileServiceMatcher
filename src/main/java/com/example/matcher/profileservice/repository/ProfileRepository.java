package com.example.matcher.profileservice.repository;

import com.example.matcher.profileservice.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, UUID> {
    boolean existsByEmail(String email);

    Optional<Profile> findByUserId(UUID userId);

    void deleteByUserId(UUID userId);
}
