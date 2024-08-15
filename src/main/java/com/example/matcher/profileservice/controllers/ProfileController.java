package com.example.matcher.profileservice.controllers;

import com.example.matcher.profileservice.dto.ProfileUpdateDTO;
import com.example.matcher.profileservice.kafka.KafkaProducerService;
import com.example.matcher.profileservice.model.Profile;
import com.example.matcher.profileservice.service.ProfileService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/v1/profile")
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/create")
    public ResponseEntity<Profile> createProfile(@RequestBody Profile profile){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) authentication.getPrincipal()); // UUID будет в качестве principal
        profile.setUserId(userId);
        return new ResponseEntity<>(profileService.createProfile(profile), HttpStatus.OK);
    }

    @PutMapping("/updateProfile")
    public ResponseEntity<Profile> updateProfile(@RequestBody ProfileUpdateDTO profile){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) authentication.getPrincipal()); // UUID будет в качестве principal
        return new ResponseEntity<>(profileService.updateProfile(userId, profile), HttpStatus.OK);
    }


    @Transactional
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) authentication.getPrincipal()); // UUID будет в качестве principal
        return new ResponseEntity<>(profileService.deleteProfile(userId), HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<List<Profile>> getAllProfiles(){
        return new ResponseEntity<>(profileService.getAllProfiles(), HttpStatus.OK);
    }

    @GetMapping("/myProfile")
    public ResponseEntity<Profile> getMyProfile(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) authentication.getPrincipal()); // UUID будет в качестве principal
        return new ResponseEntity<>(profileService.getProfileByUserId(userId), HttpStatus.OK);
    }
}
