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
    private String city;
    private String geoHash;
    @Column(unique = true)
    private String email;
    private String university;
    private String faculty;

    public static Profile profileFromDTO(ProfileCreateDTO profileCreateDTO) {
        return Profile.builder()
                .firstName(profileCreateDTO.getFirstName())
                .city(profileCreateDTO.getCity())
                .dateOfBirth(profileCreateDTO.getDateOfBirth())
                .gender(profileCreateDTO.getGender())
                .email(profileCreateDTO.getEmail())
                .university(profileCreateDTO.getUniversity())
                .faculty(profileCreateDTO.getFaculty())
                .build();
    }
}


