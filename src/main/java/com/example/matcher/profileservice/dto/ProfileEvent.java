package com.example.matcher.profileservice.dto;

import com.example.matcher.profileservice.model.Gender;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class ProfileEvent {
    private UUID userId;

    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String city;
    //    private Location

    private Byte searchAgeMin;
    private Byte searchAgeMax;
    private Gender searchGender;

    private String university;
    private String faculty;

    private String searchUniversity;
    private String searchFaculty;
}
