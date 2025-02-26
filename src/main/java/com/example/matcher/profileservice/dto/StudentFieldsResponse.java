package com.example.matcher.profileservice.dto;

import com.example.matcher.profileservice.model.StudentFields;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentFieldsResponse {
    private String university;
    private String faculty;
    private String searchUniversity;
    private String searchFaculty;

    static StudentFieldsResponse fromStudentFields(StudentFields studentFields) {
        if (studentFields == null) {
            return null;
        }
        return StudentFieldsResponse.builder()
                .university(studentFields.getUniversity())
                .faculty(studentFields.getFaculty())
                .searchUniversity(studentFields.getSearchUniversity())
                .searchFaculty(studentFields.getSearchFaculty())
                .build();
    }
}
