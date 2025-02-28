package com.example.matcher.profileservice.controllers;

import com.example.matcher.profileservice.dto.StudentConfirmationDTO;
import com.example.matcher.profileservice.model.Profile;
import com.example.matcher.profileservice.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/ProfileService/v1/student")
public class StudentController {
    private StudentService studentService;

    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);

    @Operation(summary = "Подтвердить что студент",
            description = "Подтверждение о том что пользователь студент")
    @ApiResponse(responseCode = "200", description = "Успешный ответ")
    @ApiResponse(responseCode = "404", description = "Профиль не найден", content = @Content())
    @PostMapping("/confirmation")
    public ResponseEntity<Profile> studentConfirmation(@RequestBody StudentConfirmationDTO studentConfirmation, @RequestParam UUID userId) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        UUID userId = UUID.fromString((String) authentication.getPrincipal()); // UUID будет в качестве principal
        logger.info("Event=CONFIRMATION_STUDENT, Msg='Begin confirmation student with params: {}, userId: {}'", studentConfirmation, userId);
        Profile profile = studentService.studentConfirmation(userId, studentConfirmation);
        logger.info("Event=CONFIRMATION_STUDENT, Msg='Complete confirmation student: {}'", userId);

        return new ResponseEntity<>(profile, HttpStatus.OK);
    }


}
