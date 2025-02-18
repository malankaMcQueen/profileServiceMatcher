package com.example.matcher.profileservice.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@EqualsAndHashCode(exclude = "profile")
@Entity(name = "student")
public class StudentFields {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "profile_id")  // Обеспечиваем связь с Profile
    private Profile profile; // Связь с Profile, и идентификатор будет shared (тот же самый)

    private String university;
    private String faculty;
    private Integer course;
    private String searchUniversity;
    private String searchFaculty;
    private String specialization;
    private String studentIdCardHash;
}

