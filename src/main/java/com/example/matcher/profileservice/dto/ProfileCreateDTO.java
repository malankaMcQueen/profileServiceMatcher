package com.example.matcher.profileservice.dto;

import com.example.matcher.profileservice.model.Gender;
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
}
