package com.example.matcher.profileservice.controllers;

import com.example.matcher.profileservice.dto.ProfileCreateDTO;
import com.example.matcher.profileservice.dto.ProfileUpdateDTO;
import com.example.matcher.profileservice.dto.StudentConfirmationDTO;
import com.example.matcher.profileservice.model.Profile;
import com.example.matcher.profileservice.model.StatusConnection;
import com.example.matcher.profileservice.model.StatusConnectionUpdate;
import com.example.matcher.profileservice.repository.ProfileRepository;
import com.example.matcher.profileservice.service.ProfileService;
import com.example.matcher.profileservice.service.StatusConnectionService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
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
private final ProfileRepository profileRepository;
    @PostMapping("/test")
    public ResponseEntity<Profile> createProfileTest(@RequestBody Profile profile, @RequestParam UUID userId) {
        profile.setUserId(userId);
        profile.getStudentFields().setProfile(profile);
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        UUID userId = UUID.fromString((String) authentication.getPrincipal()); // UUID будет в качестве principal
        return new ResponseEntity<>(profileRepository.save(profile), HttpStatus.OK);
    }

    @Operation(summary = "Создание профиля",
            description = "Метод создания профиля")
    @ApiResponse(responseCode = "200", description = "Успешный ответ")
    @ApiResponse(responseCode = "409", description = "Профиль с таким UUID существует", content = @Content())
    @PostMapping("/create")
    public ResponseEntity<Profile> createProfile(@RequestBody ProfileCreateDTO profile, @RequestParam UUID userId) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        UUID userId = UUID.fromString((String) authentication.getPrincipal()); // UUID будет в качестве principal
        return new ResponseEntity<>(profileService.createProfile(profile, userId), HttpStatus.OK);
    }
    @Operation(summary = "Обновить профиль",
            description = "Метод обновляет данные о профиле")
    @ApiResponse(responseCode = "200", description = "Успешный ответ")
    @ApiResponse(responseCode = "404", description = "Профиль не найден", content = @Content())
    @ApiResponse(responseCode = "400", description = "Неправильный запрос (тело запроса пустое/обновлений нет)", content = @Content())
    @PatchMapping("/updateProfile")
    public ResponseEntity<Profile> updateProfile(@RequestBody ProfileUpdateDTO profile, @RequestParam UUID userId) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        UUID userId = UUID.fromString((String) authentication.getPrincipal()); // UUID будет в качестве principal
        return new ResponseEntity<>(profileService.updateProfile(userId, profile), HttpStatus.OK);
    }




    @Operation(summary = "Удалить профиль",
            description = "Метод удаляет профиль")
    @ApiResponse(responseCode = "200", description = "Успешный ответ")
    @Transactional
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteProfile(@RequestParam UUID userId) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        UUID userId = UUID.fromString((String) authentication.getPrincipal()); // UUID будет в качестве principal
        return new ResponseEntity<>(profileService.deleteProfile(userId), HttpStatus.OK);
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
        return;
    }

    @Operation(summary = "Получить профиль с UUID:",
            description = "")
    @ApiResponse(responseCode = "200", description = "Успешный ответ")
    @GetMapping("/{userId}")
    public ResponseEntity<Profile> getProfile(@PathVariable UUID userId) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        UUID userId = UUID.fromString((String) authentication.getPrincipal()); // UUID будет в качестве principal
        return new ResponseEntity<>(profileService.getProfileByUserId(userId), HttpStatus.OK);
    }

    @Operation(summary = "Добавить фото в профиль",
            description = "")
    @ApiResponse(responseCode = "200", description = "Успешный ответ")
    @ApiResponse(responseCode = "404", description = "Профиль не найден")
    @ApiResponse(responseCode = "400", description = "Лимит по фото достигнут")
    @PostMapping("/updateProfile/addPhoto")
    public ResponseEntity<List<String>> addPhotoInProfile(@RequestParam UUID userId, @RequestParam("file") MultipartFile file) throws IOException{
        return new ResponseEntity<>(profileService.addPhotoInProfile(userId, file), HttpStatus.OK);
    }

    @Operation(summary = "Удалить фото из профиля",
            description = "")
    @ApiResponse(responseCode = "200", description = "Успешный ответ")
    @ApiResponse(responseCode = "404", description = "Профиль не найден/Фото с данным id не найдено")
    @DeleteMapping("/updateProfile/deletePhoto")
    public ResponseEntity<List<String>> deletePhotoInProfile(@RequestParam UUID userId, String link) {
        return new ResponseEntity<>(profileService.deletePhotoInProfile(userId, link), HttpStatus.OK);
    }
}
