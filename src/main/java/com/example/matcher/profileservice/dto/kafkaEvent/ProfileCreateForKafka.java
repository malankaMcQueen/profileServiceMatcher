package com.example.matcher.profileservice.dto.kafkaEvent;

import com.example.matcher.profileservice.model.Gender;
import com.example.matcher.profileservice.model.GeoPoint;
import com.example.matcher.profileservice.model.Profile;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class ProfileCreateForKafka {
    private Long id;
    private UUID userId;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String city;
    private GeoPoint geoPoint;
    private String university;
    private String faculty;

    public static ProfileCreateForKafka fromProfile(Profile profile) {
        return ProfileCreateForKafka.builder()
                .id(profile.getId())
                .userId(profile.getUserId())
                .dateOfBirth(profile.getDateOfBirth())
                .gender(profile.getGender())
                .city(profile.getCity())
                .geoPoint(profile.getGeoPoint())
                .university(profile.getUniversity())
                .faculty(profile.getFaculty())
                .build();
    }
}
