package com.example.Auto_Service_Management_System.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Mechanic {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    private String name;

    @Column
    private String specialization;

    @Column
    private String schedule;

    @OneToMany(mappedBy = "mechanic")
    private List<ServiceRequest> serviceRequests;




}
