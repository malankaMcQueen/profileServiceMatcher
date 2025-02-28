package com.example.matcher.profileservice.dto;

import com.example.matcher.profileservice.model.Gender;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ProfileUpdateDTO {
    private String firstName;
    private LocalDate dateOfBirth;
    private String city;

    private Byte searchAgeMin;
    private Byte searchAgeMax;
    private Gender searchGender;
    private StudentUpdateFields studentFields;
}
