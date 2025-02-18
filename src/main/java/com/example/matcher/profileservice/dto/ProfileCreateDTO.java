package com.example.matcher.profileservice.dto;

import com.example.matcher.profileservice.model.Gender;
import com.example.matcher.profileservice.model.Profile;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ProfileCreateDTO {
    private String firstName;
    private LocalDate dateOfBirth;
    private Gender gender;
    @Column(unique = true)
    private String email;

    public static Profile profileFromDTO(ProfileCreateDTO profileCreateDTO) {
        return Profile.builder()
                .firstName(profileCreateDTO.getFirstName())
                .dateOfBirth(profileCreateDTO.getDateOfBirth())
                .gender(profileCreateDTO.getGender())
                .email(profileCreateDTO.getEmail())
                .build();
    }
}


