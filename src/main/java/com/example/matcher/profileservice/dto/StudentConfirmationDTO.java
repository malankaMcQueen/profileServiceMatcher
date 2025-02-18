package com.example.matcher.profileservice.dto;

import com.example.matcher.profileservice.model.StudentFields;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentConfirmationDTO {
    private String university;
    private String faculty;
    private String specialization;
    private Integer course;
    private String studentIdCard;

    public static StudentFields studentFieldsFromDTO(StudentConfirmationDTO studentConfirmation) {
        return StudentFields.builder()
                .university(studentConfirmation.getUniversity())
                .faculty(studentConfirmation.getFaculty())
                .course(studentConfirmation.getCourse())
                .specialization(studentConfirmation.getSpecialization())
                .build();
    }
}
