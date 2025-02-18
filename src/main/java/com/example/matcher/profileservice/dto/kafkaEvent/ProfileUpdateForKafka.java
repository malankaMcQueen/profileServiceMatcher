package com.example.matcher.profileservice.dto.kafkaEvent;

import com.example.matcher.profileservice.model.Gender;
import com.example.matcher.profileservice.model.GeoPoint;
import com.example.matcher.profileservice.model.Profile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateForKafka {
    private Long id;
    private UUID userId;
    private LocalDate dateOfBirth;
    private String city;
    private GeoPoint geoPoint;

    private Byte searchAgeMin;
    private Byte searchAgeMax;
    private Gender searchGender;

//    private String university;
//    private String faculty;
//
//    private String searchUniversity;
//    private String searchFaculty;

    public static ProfileUpdateForKafka fromProfile(Profile profile) {
        return ProfileUpdateForKafka.builder()
                .id(profile.getId())
                .userId(profile.getUserId())
                .dateOfBirth(profile.getDateOfBirth())
                .city(profile.getCity())
                .geoPoint(profile.getGeoPoint())
                .searchAgeMin(profile.getSearchAgeMin())
                .searchAgeMax(profile.getSearchAgeMax())
                .searchGender(profile.getSearchGender())
//                .university(profile.getUniversity())
//                .faculty(profile.getFaculty())
//                .searchUniversity(profile.getSearchUniversity())
//                .searchFaculty(profile.getSearchFaculty())
                .build();
    }

}
