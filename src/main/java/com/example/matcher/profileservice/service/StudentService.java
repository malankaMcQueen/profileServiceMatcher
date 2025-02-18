package com.example.matcher.profileservice.service;

import com.example.matcher.profileservice.dto.StudentConfirmationDTO;
import com.example.matcher.profileservice.exception.BadRequestException;
import com.example.matcher.profileservice.exception.ResourceNotFoundException;
import com.example.matcher.profileservice.model.Profile;
import com.example.matcher.profileservice.model.StudentFields;
import com.example.matcher.profileservice.repository.ProfileRepository;
import com.example.matcher.profileservice.repository.StudentRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

@Service
public class StudentService {
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private StudentRepository studentRepository;

    @Value("${student.card.hash.secret}")
    private String secretKeyForHmac;

    public Profile studentConfirmation(UUID userId, StudentConfirmationDTO studentConfirmation) {

        Profile profile = profileRepository.findByUserId(userId).orElseThrow(()
                -> new ResourceNotFoundException("User not found"));
        // todo логика проверки студент ли
        StudentFields studentFields = StudentConfirmationDTO.studentFieldsFromDTO(studentConfirmation);
        // todo Изменить логику хеширования
        String studentIdCardHash = hmacSha256(studentConfirmation.getUniversity() + studentConfirmation.getStudentIdCard());

        if (studentRepository.existsByStudentIdCardHash(studentIdCardHash)) {
            throw new BadRequestException("This student Id number already used");
        }
        studentFields.setStudentIdCardHash(studentIdCardHash);

        studentFields.setProfile(profile);
        // todo Сохранение одной транзакцией
        profile.setStudentFields(studentFields);
        profile.setIsStudent(true);

        profile = profileRepository.save(profile);
        return profile;
    }



    private String hmacSha256(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secretKeyForHmac.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error generating HMAC-SHA256", e);
        }
    }

}
