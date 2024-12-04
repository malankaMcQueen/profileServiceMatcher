package com.example.matcher.profileservice.model;


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.data.repository.cdi.Eager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
    private LocalDate dateOfBirth;
    private Gender gender;
    private String city;
    private String geoHash;

    @Column(unique = true)
    private String email;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> photoLinks = new ArrayList<>();

    private Byte searchAgeMin;
    private Byte searchAgeMax;
    private Gender searchGender;

    private String university;
    private String faculty;

    private String searchUniversity;
    private String searchFaculty;
}

//public class UserParam {
//    private Long id;
//    private String userUuid;
//    private String name;
//    private LocalDate dateOfBirth;
//    private Byte searchAgeMin;
//    private Byte searchAgeMax;
//    private Gender gender;
//    private Gender searchGender;
//    private String searchUniversity;
//    private String university;
//    private String faculty;
//    private String searchFaculty;
//    private String geoHash;
//    private String city;
//    private GeoPoint location;
//}