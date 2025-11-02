package com.example.Auto_Service_Management_System.web.dto;

import lombok.*;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    @NotBlank(message = "Името е задължително")
    private String firstName;

    @NotBlank(message = "Фамилията е задължителна")
    private String lastName;

    @NotBlank(message = "Потребителското име е задължително")
    private String username;

    @Email(message = "Въведете валиден имейл")
    @NotBlank(message = "Имейлът е задължителен")
    private String email;

    @NotBlank(message = "Паролата е задължителна")
    @Size(min = 6, message = "Паролата трябва да е поне 6 символа")
    private String password;

    @Pattern(regexp = "^[0-9]{9,15}$", message = "Телефонният номер трябва да съдържа само цифри (9-15 знака)")
    @NotBlank(message = "Телефонният номер е задължителен")
    private String phoneNumber;
}
