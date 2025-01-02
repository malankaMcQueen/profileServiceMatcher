package com.example.matcher.profileservice.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(unique = true, nullable = false)
    private UUID userId;

    private String firstName;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String city;

    @JdbcTypeCode(SqlTypes.JSON)
    private GeoPoint geoPoint;

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
