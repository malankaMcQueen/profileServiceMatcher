package com.example.matcher.profileservice.dto;

import com.example.matcher.profileservice.model.Gender;
import com.example.matcher.profileservice.model.Profile;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ProfileResponse {
    private Long id;
    private UUID userId;
    private String firstName;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String city;
    private List<String> photoLinks;
    private Byte searchAgeMin;
    private Byte searchAgeMax;
    private Gender searchGender;
    private boolean activeInSearch;

    private Boolean isStudent;
    private StudentFieldsResponse studentFields;


    public static ProfileResponse fromProfile(Profile profile) {
        if (profile == null) {
            return null;
        }
        return ProfileResponse.builder()
                .id(profile.getId())
                .userId(profile.getUserId())
                .firstName(profile.getFirstName())
                .dateOfBirth(profile.getDateOfBirth())
                .gender(profile.getGender())
                .city(profile.getCity())
                .photoLinks(profile.getPhotoLinks() != null ? new ArrayList<>(profile.getPhotoLinks()) : new ArrayList<>())
                .searchAgeMax(profile.getSearchAgeMax())
                .searchAgeMin(profile.getSearchAgeMin())
                .searchGender(profile.getSearchGender())
                .isStudent(profile.getIsStudent())
                .activeInSearch(profile.getActiveInSearch())
                .studentFields(StudentFieldsResponse.fromStudentFields(profile.getStudentFields()))
                .build();
    }



}

