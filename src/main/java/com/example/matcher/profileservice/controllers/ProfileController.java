package com.example.matcher.profileservice.controllers;

import com.example.matcher.profileservice.dto.ProfileCreateDTO;
import com.example.matcher.profileservice.dto.ProfileUpdateDTO;
//import com.example.matcher.profileservice.kafka.KafkaProducerService;
import com.example.matcher.profileservice.model.Profile;
import com.example.matcher.profileservice.service.ProfileService;
import com.example.matcher.profileservice.service.S3Service;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @PostMapping("/create")
    public ResponseEntity<Profile> createProfile(@RequestBody ProfileCreateDTO profile, @RequestParam UUID userId) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        UUID userId = UUID.fromString((String) authentication.getPrincipal()); // UUID будет в качестве principal
        return new ResponseEntity<>(profileService.createProfile(profile, userId), HttpStatus.OK);
    }

    @PatchMapping("/updateProfile")
    public ResponseEntity<Profile> updateProfile(@RequestBody ProfileUpdateDTO profile, @RequestParam UUID userId) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        UUID userId = UUID.fromString((String) authentication.getPrincipal()); // UUID будет в качестве principal
        return new ResponseEntity<>(profileService.updateProfile(userId, profile), HttpStatus.OK);
    }

    @Transactional
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteProfile(@RequestParam UUID userId) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        UUID userId = UUID.fromString((String) authentication.getPrincipal()); // UUID будет в качестве principal
        return new ResponseEntity<>(profileService.deleteProfile(userId), HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<List<Profile>> getAllProfiles() {
        return new ResponseEntity<>(profileService.getAllProfiles(), HttpStatus.OK);
    }

    @GetMapping("/myProfile")
    public ResponseEntity<Profile> getMyProfile(@RequestParam UUID userId) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        UUID userId = UUID.fromString((String) authentication.getPrincipal()); // UUID будет в качестве principal
        return new ResponseEntity<>(profileService.getProfileByUserId(userId), HttpStatus.OK);
    }

    @PostMapping("/updateProfile/addPhoto")
    public ResponseEntity<List<String>> addPhotoInProfile(@RequestParam UUID userId, @RequestParam("file") MultipartFile file) throws IOException {
        return new ResponseEntity<>(profileService.addPhotoInProfile(userId, file), HttpStatus.OK);
    }

    @DeleteMapping("/updateProfile/deletePhoto")
    public ResponseEntity<List<String>> deletePhotoInProfile(@RequestParam UUID userId, String link) {
        return new ResponseEntity<>(profileService.deletePhotoInProfile(userId, link), HttpStatus.OK);
    }
}
