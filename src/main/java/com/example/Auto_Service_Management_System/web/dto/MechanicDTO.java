package com.example.Auto_Service_Management_System.web.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MechanicDTO {
    private String id;
    private String name;
    private String specialization;
    private boolean available;
}
