package com.example.matcher.profileservice.service;

import com.example.matcher.profileservice.dto.ProfileUpdateDTO;
import com.example.matcher.profileservice.exception.ResourceNotFoundException;
import com.example.matcher.profileservice.exception.UserAlreadyExistException;
import com.example.matcher.profileservice.kafka.KafkaProducerService;
import com.example.matcher.profileservice.model.Profile;
import com.example.matcher.profileservice.repository.ProfileRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final KafkaProducerService kafkaProducerService;

    public Profile createProfile(Profile profile) {
        profileRepository.findByUserId(profile.getUserId()).ifPresent(existingProfile -> {
            throw new UserAlreadyExistException("Профиль для данного пользователя уже существует.");
        });
        return profileRepository.save(profile);
    }


    public Profile updateProfile(UUID userId, ProfileUpdateDTO profileUpdate) {
        Profile profile = profileRepository.findByUserId(userId).orElseThrow(()
                -> new ResourceNotFoundException("Пользователь с данным ID не найден."));

        Optional.ofNullable(profileUpdate.getFirstName()).ifPresent(profile::setFirstName);
        Optional.ofNullable(profileUpdate.getLastName()).ifPresent(profile::setLastName);
        Optional.ofNullable(profileUpdate.getDateOfBirth()).ifPresent(profile::setDateOfBirth);
        Optional.ofNullable(profileUpdate.getCity()).ifPresent(profile::setCity);
        Optional.ofNullable(profileUpdate.getSearchAgeMin()).ifPresent(profile::setSearchAgeMin);
        Optional.ofNullable(profileUpdate.getSearchAgeMax()).ifPresent(profile::setSearchAgeMax);
        Optional.ofNullable(profileUpdate.getSearchGender()).ifPresent(profile::setSearchGender);
        Optional.ofNullable(profileUpdate.getSearchUniversity()).ifPresent(profile::setSearchUniversity);
        Optional.ofNullable(profileUpdate.getSearchFaculty()).ifPresent(profile::setSearchFaculty);
        return profile;
    }

    public Object deleteProfile(UUID userId) {
        profileRepository.deleteByUserId(userId);
        kafkaProducerService.sendMessage(userId.toString(), "delete_profile");
        return "Success";
    }

    public List<Profile> getAllProfiles() {
        return profileRepository.findAll();
    }

    public Profile getProfileByUserId(UUID userId) {
        return profileRepository.findByUserId(userId).orElseThrow(()
                -> new ResourceNotFoundException("Пользователь с данным ID не найден."));
    }
}
