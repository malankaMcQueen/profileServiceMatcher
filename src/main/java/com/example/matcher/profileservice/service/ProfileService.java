package com.example.matcher.profileservice.service;

import com.example.matcher.profileservice.aspect.AspectAnnotation;
import com.example.matcher.profileservice.dto.*;

import com.example.matcher.profileservice.dto.kafkaEvent.ProfileUpdateForKafka;
import com.example.matcher.profileservice.exception.BadRequestException;
import com.example.matcher.profileservice.exception.ResourceNotFoundException;
import com.example.matcher.profileservice.exception.UserAlreadyExistException;
import com.example.matcher.profileservice.kafka.KafkaProducerService;
import com.example.matcher.profileservice.model.Profile;
import com.example.matcher.profileservice.repository.ProfileRepository;
import com.example.matcher.profileservice.service.utils.ProfileUtils;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import java.util.stream.Collectors;

import static com.example.matcher.profileservice.service.ImageProcessor.processImageWithThumbnailator;

@Service
@AllArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final S3Service s3Service;
    private final KafkaProducerService kafkaProducerService;
    private final GeoHashService geoHashService;
    private static final Logger logger = LoggerFactory.getLogger(ProfileService.class);

    public ProfileResponse createProfile(ProfileCreateDTO profileCreateDTO, UUID userId) {
        profileRepository.findByUserId(userId).ifPresent(existingProfile -> {
            throw new UserAlreadyExistException("Профиль для данного пользователя уже существует.");
        });

        Profile profile = ProfileCreateDTO.profileFromDTO(profileCreateDTO);
        profile.setUserId(userId);
        profile.setIsStudent(false);
        profile.setActiveInSearch(false);

        profile = profileRepository.save(profile);

//        ProfileCreateForKafka profileCreateForKafka = ProfileCreateForKafka.fromProfile(profile);
//        kafkaProducerService.sendMessage(profileCreateForKafka, "create_profile");

        return ProfileResponse.fromProfile(profile);
    }

    public List<String> addPhotoInProfile(UUID userId, MultipartFile file) throws IOException {
        Profile profile = profileRepository.findByUserId(userId).orElseThrow(()
                -> new ResourceNotFoundException("User not found."));
        List<String> photoLinks = profile.getPhotoLinks();
        if (photoLinks.size() >= 6) {
            throw new BadRequestException("Photo max size 6");
        }
        // Обработка изображения
        File processedFile = processImageWithThumbnailator(file);
        String link = s3Service.uploadFile(userId, processedFile);
        if (!processedFile.delete()) {
            logger.warn("Failed to delete temporary processed file: " + processedFile.getAbsolutePath());
        }

        photoLinks.add(link);
        profile.setPhotoLinks(photoLinks);

        boolean profileBeenActive = profile.getActiveInSearch();
        profile.setActiveInSearch(profileIsReadyForSearch(profile));

        profileRepository.save(profile);

        processUpdateForKafkaEvent(profile, profileBeenActive);
        if (profileBeenActive != profile.getActiveInSearch()) {
            processUpdateForKafkaEvent(profile, profileBeenActive);
        }

        return photoLinks;
    }

    public List<String> deletePhotoInProfile(UUID userId, String link) {
        Profile profile = profileRepository.findByUserId(userId).orElseThrow(()
                -> new ResourceNotFoundException("Пользователь с данным ID не найден."));
        List<String> photoLinks = profile.getPhotoLinks();
        if (!photoLinks.contains(link)) {
            throw new ResourceNotFoundException("Link not found");
        }
        photoLinks.remove(link);
        profile.setPhotoLinks(photoLinks);

        boolean profileBeenActive = profile.getActiveInSearch();
        profile.setActiveInSearch(profileIsReadyForSearch(profile));

        profileRepository.save(profile);
        s3Service.deleteFile(link);

        if (profileBeenActive != profile.getActiveInSearch()) {
            processUpdateForKafkaEvent(profile, profileBeenActive);
        }
        return photoLinks;
    }


    public ProfileResponse updateProfile(UUID userId, ProfileUpdateDTO profileUpdate) {

        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с данным ID не найден."));
        // Обновление полей
        boolean profileIsUpdate = refreshProfileFromDTO(profile, profileUpdate);

        // Если изменений не было
        if (!profileIsUpdate) {
            return ProfileResponse.fromProfile(profile);
        }

        boolean activityBeforeUpdate = profile.getActiveInSearch();
        profile.setActiveInSearch(profileIsReadyForSearch(profile));
        profileRepository.save(profile);

        processUpdateForKafkaEvent(profile, activityBeforeUpdate);

        return ProfileResponse.fromProfile(profile);
    }

    private void processUpdateForKafkaEvent(Profile profile, boolean activityBeforeUpdate) {
        if (activityBeforeUpdate && profile.getActiveInSearch()) {
            ProfileUpdateForKafka profileEvent = ProfileUpdateForKafka.fromProfile(profile);
            kafkaProducerService.sendMessage(profileEvent, "update_profile");
        }
        else if (!activityBeforeUpdate && profile.getActiveInSearch()) {
            ProfileUpdateForKafka profileEvent = ProfileUpdateForKafka.fromProfile(profile);
            kafkaProducerService.sendMessage(profileEvent, "active_profile");
        }
        else if (activityBeforeUpdate && !profile.getActiveInSearch()) {
            kafkaProducerService.sendMessage(profile.getUserId().toString(), "passive_profile");
        }
    }

    private boolean refreshProfileFromDTO(Profile profile, ProfileUpdateDTO profileUpdate) {
        boolean isUpdated = ProfileUtils.updateField(profileUpdate.getFirstName(), profile::getFirstName, profile::setFirstName)
                | ProfileUtils.updateField(profileUpdate.getDateOfBirth(), profile::getDateOfBirth, profile::setDateOfBirth)
                | ProfileUtils.updateField(profileUpdate.getCity(), profile::getCity, profile::setCity)
                | ProfileUtils.updateField(profileUpdate.getSearchAgeMin(), profile::getSearchAgeMin, profile::setSearchAgeMin)
                | ProfileUtils.updateField(profileUpdate.getSearchAgeMax(), profile::getSearchAgeMax, profile::setSearchAgeMax)
                | ProfileUtils.updateField(profileUpdate.getSearchGender(), profile::getSearchGender, profile::setSearchGender);
        if (profile.getIsStudent() && profileUpdate.getStudentFields() != null) {
            isUpdated = isUpdated | ProfileUtils.updateField(profileUpdate.getStudentFields().getSearchFaculty(),
                    profile.getStudentFields()::getSearchFaculty, profile.getStudentFields()::setSearchUniversity)
                    | ProfileUtils.updateField(profileUpdate.getStudentFields().getSearchUniversity(),
                    profile.getStudentFields()::getSearchUniversity, profile.getStudentFields()::setSearchUniversity);
        }
        return isUpdated;
    }

    // todo Сделать логику проверки
    private boolean profileIsReadyForSearch(Profile profile) {
        return (/*!profile.getPhotoLinks().isEmpty() && profile.getGeoPoint() != null
                &&*/ profile.getFirstName() != null && profile.getCity() != null);
    }


    @AspectAnnotation
    public Object deleteProfile(UUID userId) {
        profileRepository.deleteByUserId(userId);   // todo Почистить хранилище s3?
        kafkaProducerService.sendMessage(userId.toString(), "delete_profile");
        return "Success";
    }

    public List<Profile> getAllProfiles() {
        return profileRepository.findAll();
    }

    public ProfileResponse getProfileByUserId(UUID userId) {
        return ProfileResponse.fromProfile(profileRepository.findByUserId(userId).orElseThrow(()
                -> new ResourceNotFoundException("Пользователь с данным ID не найден.")));
    }


    public List<ProfileSelectionResponse> getListProfiles(UserIdsRequest userIds) {
        return profileRepository.findProfilesByList(
                userIds.getUserIds().stream()
                        .map(userId -> {
                            try {
                                return UUID.fromString(userId);
                            } catch (IllegalArgumentException e) {
                                return null; // Ошибочные строки превращаем в null
                            }
                        })
                        .filter(Objects::nonNull) // Удаляем null-значения
                        .collect(Collectors.toList())
        ).stream().map(ProfileSelectionResponse::fromProfile).collect(Collectors.toList());
    }

    public ProfileResponse updateGeoPointProfile(UUID userId, String geoHash) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с данным ID не найден."));
        profile.setGeoPoint(geoHashService.decodeGeoHash(geoHash));
        return ProfileResponse.fromProfile(profileRepository.save(profile));
    }
}
