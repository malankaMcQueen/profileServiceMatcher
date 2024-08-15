package com.example.matcher.profileservice.dto;

import jakarta.persistence.Column;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class ProfileUpdateDTO {
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String city;
    //    private Location

    private Byte searchAgeMin;
    private Byte searchAgeMax;
    private String searchGender;

    private String searchUniversity;
    private String searchFaculty;
}
