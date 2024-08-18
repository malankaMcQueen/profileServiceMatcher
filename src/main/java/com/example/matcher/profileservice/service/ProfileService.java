package com.example.matcher.profileservice.service;

import com.example.matcher.profileservice.dto.ProfileEvent;

import com.example.matcher.profileservice.dto.ProfileUpdateDTO;
import com.example.matcher.profileservice.exception.ResourceNotFoundException;
import com.example.matcher.profileservice.exception.UserAlreadyExistException;
import com.example.matcher.profileservice.kafka.KafkaProducerService;
import com.example.matcher.profileservice.model.Profile;
import com.example.matcher.profileservice.repository.ProfileRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
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
        ProfileEvent profileEvent = new ProfileEvent();
        BeanUtils.copyProperties(profile, profileEvent);
        kafkaProducerService.sendMessage(profileEvent, "create_profile");
        return profileRepository.save(profile);
    }

    public Profile updateProfile(UUID userId, ProfileUpdateDTO profileUpdate) {
        // Проверка, что DTO не пустое
        if (profileUpdate == null ||
                (profileUpdate.getFirstName() == null &&
                        profileUpdate.getLastName() == null &&
                        profileUpdate.getDateOfBirth() == null &&
                        profileUpdate.getCity() == null &&
                        profileUpdate.getSearchAgeMin() == null &&
                        profileUpdate.getSearchAgeMax() == null &&
                        profileUpdate.getSearchGender() == null &&
                        profileUpdate.getSearchUniversity() == null &&
                        profileUpdate.getSearchFaculty() == null)) {
            throw new IllegalArgumentException("Profile update data is empty or null.");
        }
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с данным ID не найден."));
        // Флаг для проверки наличия изменений
        boolean isUpdated = false;
        // Проверка и обновление полей
        if (!Objects.equals(profileUpdate.getFirstName(), profile.getFirstName())) {
            Optional.ofNullable(profileUpdate.getFirstName()).ifPresent(profile::setFirstName);
            isUpdated = true;
        }
        if (!Objects.equals(profileUpdate.getLastName(), profile.getLastName())) {
            Optional.ofNullable(profileUpdate.getLastName()).ifPresent(profile::setLastName);
            isUpdated = true;
        }
        if (!Objects.equals(profileUpdate.getDateOfBirth(), profile.getDateOfBirth())) {
            Optional.ofNullable(profileUpdate.getDateOfBirth()).ifPresent(profile::setDateOfBirth);
            isUpdated = true;
        }
        if (!Objects.equals(profileUpdate.getCity(), profile.getCity())) {
            Optional.ofNullable(profileUpdate.getCity()).ifPresent(profile::setCity);
            isUpdated = true;
        }
        if (!Objects.equals(profileUpdate.getSearchAgeMin(), profile.getSearchAgeMin())) {
            Optional.ofNullable(profileUpdate.getSearchAgeMin()).ifPresent(profile::setSearchAgeMin);
            isUpdated = true;
        }
        if (!Objects.equals(profileUpdate.getSearchAgeMax(), profile.getSearchAgeMax())) {
            Optional.ofNullable(profileUpdate.getSearchAgeMax()).ifPresent(profile::setSearchAgeMax);
            isUpdated = true;
        }
        if (!Objects.equals(profileUpdate.getSearchGender(), profile.getSearchGender())) {
            Optional.ofNullable(profileUpdate.getSearchGender()).ifPresent(profile::setSearchGender);
            isUpdated = true;
        }
        if (!Objects.equals(profileUpdate.getSearchUniversity(), profile.getSearchUniversity())) {
            Optional.ofNullable(profileUpdate.getSearchUniversity()).ifPresent(profile::setSearchUniversity);
            isUpdated = true;
        }
        if (!Objects.equals(profileUpdate.getSearchFaculty(), profile.getSearchFaculty())) {
            Optional.ofNullable(profileUpdate.getSearchFaculty()).ifPresent(profile::setSearchFaculty);
            isUpdated = true;
        }
        // Если обновлений не произошло, возвращаем текущий профиль без изменений
        if (!isUpdated) {
            throw new IllegalArgumentException("No updates were made. The provided data matches the current profile.");
        }

        profileRepository.save(profile);
        ProfileEvent profileEvent = new ProfileEvent();
        BeanUtils.copyProperties(profile, profileEvent);
        kafkaProducerService.sendMessage(profileEvent, "profile_update");
        return profile;
    }

//    public Profile updateProfile(UUID userId, ProfileUpdateDTO profileUpdate) {
//        Profile profile = profileRepository.findByUserId(userId).orElseThrow(()
//                -> new ResourceNotFoundException("Пользователь с данным ID не найден."));
//
//        Optional.ofNullable(profileUpdate.getFirstName()).ifPresent(profile::setFirstName);
//        Optional.ofNullable(profileUpdate.getLastName()).ifPresent(profile::setLastName);
//        Optional.ofNullable(profileUpdate.getDateOfBirth()).ifPresent(profile::setDateOfBirth);
//        Optional.ofNullable(profileUpdate.getCity()).ifPresent(profile::setCity);
//        Optional.ofNullable(profileUpdate.getSearchAgeMin()).ifPresent(profile::setSearchAgeMin);
//        Optional.ofNullable(profileUpdate.getSearchAgeMax()).ifPresent(profile::setSearchAgeMax);
//        Optional.ofNullable(profileUpdate.getSearchGender()).ifPresent(profile::setSearchGender);
//        Optional.ofNullable(profileUpdate.getSearchUniversity()).ifPresent(profile::setSearchUniversity);
//        Optional.ofNullable(profileUpdate.getSearchFaculty()).ifPresent(profile::setSearchFaculty);
//        profileRepository.save(profile);
//ProfileEvent profileEvent = new ProfileEvent();
//        BeanUtils.copyProperties(profile, profileEvent);
//        kafkaProducerService.sendMessage(profileEvent, "profile_update");
//        // send event
//        return profile;
//    }

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
