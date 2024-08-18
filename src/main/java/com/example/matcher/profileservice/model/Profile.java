package com.example.matcher.profileservice.model;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Entity
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(unique = true, nullable = false)
    private UUID userId;

    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String city;
//    private Location
    @Column(unique = true)
    private String email;

    private Byte searchAgeMin;
    private Byte searchAgeMax;
    private Gender searchGender;

    private String university;
    private String faculty;

    private String searchUniversity;
    private String searchFaculty;

}