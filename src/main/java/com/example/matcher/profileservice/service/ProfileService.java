package com.example.matcher.profileservice.service;

import com.example.matcher.profileservice.aspect.AspectAnnotation;
import com.example.matcher.profileservice.dto.ProfileCreateDTO;
import com.example.matcher.profileservice.dto.kafkaEvent.ProfileCreateForKafka;
import com.example.matcher.profileservice.dto.kafkaEvent.ProfileEvent;

import com.example.matcher.profileservice.dto.ProfileUpdateDTO;
import com.example.matcher.profileservice.dto.kafkaEvent.ProfileUpdateForKafka;
import com.example.matcher.profileservice.exception.BadRequestException;
import com.example.matcher.profileservice.exception.ResourceNotFoundException;
import com.example.matcher.profileservice.exception.UserAlreadyExistException;
import com.example.matcher.profileservice.kafka.KafkaProducerService;
import com.example.matcher.profileservice.model.Profile;
import com.example.matcher.profileservice.repository.ProfileRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.example.matcher.profileservice.service.ImageProcessor.processImageWithThumbnailator;

@Service
@AllArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final S3Service s3Service;
    private final GeoHashService geoHashService;

    private final KafkaProducerService kafkaProducerService;

    private static final Logger logger = LoggerFactory.getLogger(S3Service.class);

    public Profile createProfile(ProfileCreateDTO profileCreateDTO, UUID userId) {
        profileRepository.findByUserId(userId).ifPresent(existingProfile -> {
            throw new UserAlreadyExistException("Профиль для данного пользователя уже существует.");
        });

        Profile profile = ProfileCreateDTO.profileFromDTO(profileCreateDTO);
        profile.setUserId(userId);
        profile.setGeoPoint(geoHashService.decodeGeoHash(profileCreateDTO.getGeoHash()));

        profile = profileRepository.save(profile);
        ProfileCreateForKafka profileCreateForKafka = ProfileCreateForKafka.fromProfile(profile);
        kafkaProducerService.sendMessage(profileCreateForKafka, "create_profile");
        return profileRepository.save(profile);
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
        profileRepository.save(profile);
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
        profileRepository.save(profile);
        s3Service.deleteFile(link);
        return photoLinks;
    }


    public Profile updateProfile(UUID userId, ProfileUpdateDTO profileUpdate) {
        // Проверка, что DTO не пустое
        if (isProfileUpdateDTOEmpty(profileUpdate)) {
            throw new BadRequestException("Profile update data is empty or null.");
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
            throw new BadRequestException("No updates were made. The provided data matches the current profile.");
        }
        // Сохранение изменений
        profileRepository.save(profile);
        // Отправка события через Kafka
        ProfileUpdateForKafka profileEvent = ProfileUpdateForKafka.fromProfile(profile);
        kafkaProducerService.sendMessage(profileEvent, "update_profile");
        return profile;
    }

    private boolean isProfileUpdateDTOEmpty(ProfileUpdateDTO profileUpdate) {
        return profileUpdate == null || (
                profileUpdate.getCity() == null &&
                        profileUpdate.getSearchAgeMin() == null &&
                        profileUpdate.getSearchAgeMax() == null &&
                        profileUpdate.getSearchUniversity() == null &&
                        profileUpdate.getSearchFaculty() == null &&
                        profileUpdate.getSearchGender() == null &&
                        profileUpdate.getFirstName() == null &&
                        profileUpdate.getDateOfBirth() == null &&
                        profileUpdate.getUniversity() == null &&
                        profileUpdate.getFaculty() == null
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
