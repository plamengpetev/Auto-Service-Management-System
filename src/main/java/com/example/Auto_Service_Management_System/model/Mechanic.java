package com.example.Auto_Service_Management_System.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Mechanic {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String specialization;

    @Column
    private String schedule;

    @OneToMany(mappedBy = "mechanic", fetch = FetchType.LAZY)
    private List<ServiceRequest> serviceRequests;
}
