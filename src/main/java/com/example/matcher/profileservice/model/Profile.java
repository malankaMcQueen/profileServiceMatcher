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
@Table(schema = "profile", name = "profile")
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    private Boolean activeInSearch = false;
    private Boolean isStudent = false;

    @OneToOne(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private StudentFields studentFields;

//    private boolean isStudent;

//    private String university;
//    private String faculty;
//
//    private String searchUniversity;
//    private String searchFaculty;
}
