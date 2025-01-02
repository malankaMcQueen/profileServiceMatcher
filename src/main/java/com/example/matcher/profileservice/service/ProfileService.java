package com.example.matcher.profileservice.service;

import com.example.matcher.profileservice.aspect.AspectAnnotation;
import com.example.matcher.profileservice.dto.ProfileCreateDTO;
import com.example.matcher.profileservice.dto.ProfileEvent;

import com.example.matcher.profileservice.dto.ProfileUpdateDTO;
import com.example.matcher.profileservice.exception.BadRequestException;
import com.example.matcher.profileservice.exception.ResourceNotFoundException;
import com.example.matcher.profileservice.exception.UserAlreadyExistException;
//import com.example.matcher.profileservice.kafka.KafkaProducerService;
import com.example.matcher.profileservice.kafka.KafkaProducerService;
import com.example.matcher.profileservice.model.Profile;
import com.example.matcher.profileservice.repository.ProfileRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Service
@AllArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final S3Service s3Service;
    private final GeoHashService geoHashService;

    private final KafkaProducerService kafkaProducerService;

    public Profile createProfile(ProfileCreateDTO profileCreateDTO, UUID userId) {
        profileRepository.findByUserId(userId).ifPresent(existingProfile -> {
            throw new UserAlreadyExistException("Профиль для данного пользователя уже существует.");
        });
        Profile profile = Profile.builder()
                .userId(userId)
                .firstName(profileCreateDTO.getFirstName())
                .city(profileCreateDTO.getCity())
                .dateOfBirth(profileCreateDTO.getDateOfBirth())
                .gender(profileCreateDTO.getGender())
                .email(profileCreateDTO.getEmail())
                .university(profileCreateDTO.getUniversity())
                .faculty(profileCreateDTO.getFaculty())
                .geoPoint(geoHashService.decodeGeoHash(profileCreateDTO.getGeoHash()))
                .build();

        profile = profileRepository.save(profile);
        ProfileEvent profileEvent = new ProfileEvent();
        profileEvent = ProfileEvent.fromProfile(profile);

        kafkaProducerService.sendMessage(profileEvent, "create_profile");
        return profileRepository.save(profile);
    }

    @Transactional
    public List<String> addPhotoInProfile(UUID userId, MultipartFile file) throws IOException {
        Profile profile = profileRepository.findByUserId(userId).orElseThrow(()
                -> new ResourceNotFoundException("User not found."));
        List<String> photoLinks = profile.getPhotoLinks();
        if (photoLinks.size() >= 6) {
            throw new BadRequestException("Photo max size 6");
        }
        String link = s3Service.uploadFile(userId, file);
        photoLinks.add(link);
        profile.setPhotoLinks(photoLinks);
        profileRepository.save(profile);
        return photoLinks;
    }

    public List<String> deletePhotoInProfile(UUID userId, String link) {
        Profile profile = profileRepository.findByUserId(userId).orElseThrow(()
                -> new ResourceNotFoundException("Пользователь с данным ID не найден."));
        List<String> photoLinks = profile.getPhotoLinks();
//        if (!photoLinks.contains(link)) {
//            throw new ResourceNotFoundException("Link not found");
//        }
        photoLinks.remove(link);
        profile.setPhotoLinks(photoLinks);
        profileRepository.save(profile);
        s3Service.deleteFile(link);
        return photoLinks;
    }


    public Profile updateProfile(UUID userId, ProfileUpdateDTO profileUpdate) {
        // Проверка, что DTO не пустое
        if (isProfileUpdateDTOEmpty(profileUpdate)) {
            throw new IllegalArgumentException("Profile update data is empty or null.");
        }
        // Получение профиля пользователя
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с данным ID не найден."));
        // Обновление полей
        boolean isUpdated = updateField(profileUpdate.getFirstName(), profile::getFirstName, profile::setFirstName)
                | updateField(profileUpdate.getDateOfBirth(), profile::getDateOfBirth, profile::setDateOfBirth)
                | updateField(profileUpdate.getCity(), profile::getCity, profile::setCity)
                | updateField(profileUpdate.getSearchAgeMin(), profile::getSearchAgeMin, profile::setSearchAgeMin)
                | updateField(profileUpdate.getSearchAgeMax(), profile::getSearchAgeMax, profile::setSearchAgeMax)
                | updateField(profileUpdate.getSearchGender(), profile::getSearchGender, profile::setSearchGender)
                | updateField(profileUpdate.getSearchUniversity(), profile::getSearchUniversity, profile::setSearchUniversity)
                | updateField(profileUpdate.getSearchFaculty(), profile::getSearchFaculty, profile::setSearchFaculty);

        // Если изменений не было
        if (!isUpdated) {
            throw new IllegalArgumentException("No updates were made. The provided data matches the current profile.");
        }
        // Сохранение изменений
        profileRepository.save(profile);
        // Отправка события через Kafka
        ProfileEvent profileEvent = new ProfileEvent();
        profileEvent = ProfileEvent.fromProfile(profile);
        kafkaProducerService.sendMessage(profileEvent, "profile_update");
        return profile;
    }

    private boolean isProfileUpdateDTOEmpty(ProfileUpdateDTO profileUpdate) {
        return profileUpdate == null || (
                profileUpdate.getFirstName() == null &&
                        profileUpdate.getDateOfBirth() == null &&
                        profileUpdate.getCity() == null &&
                        profileUpdate.getSearchAgeMin() == null &&
                        profileUpdate.getSearchAgeMax() == null &&
                        profileUpdate.getSearchGender() == null &&
                        profileUpdate.getSearchUniversity() == null &&
                        profileUpdate.getSearchFaculty() == null
        );
    }

    private <T> boolean updateField(T newValue, Supplier<T> getter, Consumer<T> setter) {
        if (!Objects.equals(newValue, getter.get())) {
            Optional.ofNullable(newValue).ifPresent(setter);
            return true; // Было обновление
        }
        return false; // Обновления не было
    }

    @AspectAnnotation
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
