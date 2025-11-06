package com.example.Auto_Service_Management_System.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    @Email(message = "Въведи валиден имейл адрес")
    @NotBlank(message = "Имейлът е задължителен")
    private String email;

    @NotBlank(message = "Паролата е задължителна")
    private String password;
}
