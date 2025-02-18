package com.example.matcher.profileservice.repository;

import com.example.matcher.profileservice.model.StudentFields;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<StudentFields, Long> {
    Boolean existsByStudentIdCardHash(String studentIdNumberHash);
}
