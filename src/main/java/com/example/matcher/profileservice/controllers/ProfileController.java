package com.example.matcher.profileservice.controllers;

import com.example.matcher.profileservice.dto.*;
import com.example.matcher.profileservice.model.Profile;
import com.example.matcher.profileservice.model.StatusConnectionUpdate;
import com.example.matcher.profileservice.service.ProfileService;
import com.example.matcher.profileservice.service.StatusConnectionService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/ProfileService/v1/profile")
public class ProfileController {

    private final ProfileService profileService;
    private final StatusConnectionService statusConnectionService;
    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);


    @Operation(summary = "Создание профиля",
            description = "Метод создания профиля")
    @ApiResponse(responseCode = "200", description = "Успешный ответ")
    @ApiResponse(responseCode = "409", description = "Профиль с таким UUID существует", content = @Content())
    @PostMapping("/create")
    public ResponseEntity<ProfileResponse> createProfile(@RequestBody ProfileCreateDTO profile, @RequestParam UUID userId) {
        logger.info("Event=CREATE_PROFILE, Msg='Begin create profile with params: {}, userId: {}'", profile, userId);
        ProfileResponse profileResponse = profileService.createProfile(profile, userId);
        logger.info("Event=CREATE_PROFILE, Msg='Complete create profile");

//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        UUID userId = UUID.fromString((String) authentication.getPrincipal()); // UUID будет в качестве principal
        return new ResponseEntity<>(profileResponse, HttpStatus.OK);
    }
    @Operation(summary = "Обновить профиль",
            description = "Метод обновляет данные о профиле")
    @ApiResponse(responseCode = "200", description = "Успешный ответ")
    @ApiResponse(responseCode = "404", description = "Профиль не найден", content = @Content())
    @PatchMapping("/updateProfile")
    public ResponseEntity<ProfileResponse> updateProfile(@RequestBody ProfileUpdateDTO profile, @RequestParam UUID userId) {
        logger.info("Event=UPDATE_PROFILE, Msg='Begin update profile with params: {}, userId: {}'", profile, userId);
        ProfileResponse profileResponse = profileService.updateProfile(userId, profile);
        logger.info("Event=UPDATE_PROFILE, Msg='Complete update profile");
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        UUID userId = UUID.fromString((String) authentication.getPrincipal()); // UUID будет в качестве principal
        return new ResponseEntity<>(profileResponse, HttpStatus.OK);
    }


    @Operation(summary = "Обновить координаты пользователя",
            description = "Метод обновляет координаты пользователя")
    @ApiResponse(responseCode = "200", description = "Успешный ответ")
    @ApiResponse(responseCode = "404", description = "Профиль не найден", content = @Content())
    @PatchMapping("/updateGeoPoint")
    public ResponseEntity<ProfileResponse> updateGeoPoint(@RequestParam String geoHash, @RequestParam UUID userId) {
        logger.info("Event=UPDATE_GEO_POINT_PROFILE, Msg='Begin update geo point profile with params: {}, userId: {}'", geoHash, userId);
        ProfileResponse profileResponse = profileService.updateGeoPointProfile(userId, geoHash);
        logger.info("Event=UPDATE_GEO_POINT_PROFILE, Msg='Complete update geo point profile");
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        UUID userId = UUID.fromString((String) authentication.getPrincipal()); // UUID будет в качестве principal
        return new ResponseEntity<>(profileResponse, HttpStatus.OK);
    }


    @Operation(summary = "Удалить профиль",
            description = "Метод удаляет профиль")
    @ApiResponse(responseCode = "200", description = "Успешный ответ")
    @Transactional
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteProfile(@RequestParam UUID userId) {
        logger.info("Event=DELETE_PROFILE, Msg='Begin delete profile with params userId: {}'", userId);
        profileService.deleteProfile(userId);
        logger.info("Event=DELETE_PROFILE, Msg='Delete success userId: {}'", userId);

        //        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        UUID userId = UUID.fromString((String) authentication.getPrincipal()); // UUID будет в качестве principal
        return new ResponseEntity<>("Success", HttpStatus.OK);
    }

    @Operation(summary = "Получить все существующие профили",
            description = "")
    @ApiResponse(responseCode = "200", description = "Успешный ответ")
    @GetMapping()
    public ResponseEntity<List<Profile>> getAllProfiles() {
        return new ResponseEntity<>(profileService.getAllProfiles(), HttpStatus.OK);
    }

    @Hidden
    @GetMapping("/statusUsers")
    public ResponseEntity<List<StatusConnectionUpdate>> getUserStatus() {
        return new ResponseEntity<>(statusConnectionService.getAllOnlineUsers(), HttpStatus.OK);
    }

    @Hidden
    @PostMapping("/statusUsers/update")
    public void updateConnectionStatus(@RequestBody StatusConnectionUpdate statusConnectionUpdate) {
        statusConnectionService.statusConnectionUpdate(statusConnectionUpdate);
    }

    @Operation(summary = "Получить профиль с UUID:",
            description = "")
    @ApiResponse(responseCode = "200", description = "Успешный ответ")
    @GetMapping("/{userId}")
    public ResponseEntity<ProfileResponse> getProfile(@PathVariable UUID userId) {
        logger.info("Event=GET_PERSONAL_PROFILE, Msg='Begin get profile: {}'", userId);
        ProfileResponse profileResponse = profileService.getProfileByUserId(userId);
        logger.info("Event=GET_PERSONAL_PROFILE, Msg='Get profile completed: {}'", userId);
        //        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        UUID userId = UUID.fromString((String) authentication.getPrincipal()); // UUID будет в качестве principal
        return new ResponseEntity<>(profileResponse, HttpStatus.OK);
    }

    @Operation(summary = "Добавить фото в профиль",
            description = "")
    @ApiResponse(responseCode = "200", description = "Успешный ответ")
    @ApiResponse(responseCode = "404", description = "Профиль не найден")
    @ApiResponse(responseCode = "400", description = "Лимит по фото достигнут")
    @PostMapping("/updateProfile/addPhoto")
    public ResponseEntity<List<String>> addPhotoInProfile(@RequestParam UUID userId, @RequestParam("file") MultipartFile file) throws IOException{
        logger.info("Event=UPLOAD_PHOTO_PROFILE, Msg='Begin upload photo in profile: {}'", userId);
        List<String> photoList = profileService.addPhotoInProfile(userId, file);
        logger.info("Event=UPLOAD_PHOTO_PROFILE, Msg='Upload photo completed: {}'", userId);
        return new ResponseEntity<>(photoList, HttpStatus.OK);
    }

    @Operation(summary = "Удалить фото из профиля",
            description = "")
    @ApiResponse(responseCode = "200", description = "Успешный ответ")
    @ApiResponse(responseCode = "404", description = "Профиль не найден/Фото с данным id не найдено")
    @DeleteMapping("/updateProfile/deletePhoto")
    public ResponseEntity<List<String>> deletePhotoInProfile(@RequestParam UUID userId, String link) {
        logger.info("Event=DELETE_PHOTO_PROFILE, Msg='Begin delete photo in profile: {}'", userId);
        List<String> photoList = profileService.deletePhotoInProfile(userId, link);
        logger.info("Event=DELETE_PHOTO_PROFILE, Msg='Delete photo completed: {}'", userId);

        return new ResponseEntity<>(photoList, HttpStatus.OK);
    }

    @Operation(summary = "Получить список профилей по списку UUID:",
            description = "")
    @ApiResponse(responseCode = "200", description = "Успешный ответ")
    @GetMapping("/list")
    public ResponseEntity<List<ProfileSelectionResponse>> getProfile(@RequestBody UserIdsRequest userIds) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        UUID userId = UUID.fromString((String) authentication.getPrincipal()); // UUID будет в качестве principal
        logger.info("Event=GET_LIST_PROFILES, Msg='Begin get list profiles from userIds: {}'", userIds);
        List<ProfileSelectionResponse> profiles = profileService.getListProfiles(userIds);
        logger.info("Event=GET_LIST_PROFILES, Msg='Complete get list'");
        return new ResponseEntity<>(profiles, HttpStatus.OK);
    }
}
