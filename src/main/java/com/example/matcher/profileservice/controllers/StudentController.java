package com.example.matcher.profileservice.controllers;

import com.example.matcher.profileservice.dto.StudentConfirmationDTO;
import com.example.matcher.profileservice.model.Profile;
import com.example.matcher.profileservice.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/ProfileService/v1/student")
public class StudentController {
    private StudentService studentService;


    @Operation(summary = "Подтвердить что студент",
            description = "Подтверждение о том что пользователь студент")
    @ApiResponse(responseCode = "200", description = "Успешный ответ")
    @ApiResponse(responseCode = "404", description = "Профиль не найден", content = @Content())
    @PostMapping("/confirmation")
    public ResponseEntity<Profile> studentConfirmation(@RequestBody StudentConfirmationDTO studentConfirmation, @RequestParam UUID userId) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        UUID userId = UUID.fromString((String) authentication.getPrincipal()); // UUID будет в качестве principal
        return new ResponseEntity<>(studentService.studentConfirmation(userId, studentConfirmation), HttpStatus.OK);
    }


}
