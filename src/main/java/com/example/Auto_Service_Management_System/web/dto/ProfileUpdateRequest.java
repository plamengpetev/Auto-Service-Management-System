package com.example.Auto_Service_Management_System.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileUpdateRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Pattern(regexp = "^[0-9]{9,15}$", message = "Phone number must contain only digits (9–15 characters)")
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
}
