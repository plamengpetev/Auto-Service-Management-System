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
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false, unique = true)
    private String registrationNumber;


    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer owner;

    @OneToMany(mappedBy = "vehicle", fetch = FetchType.EAGER)
    private List<ServiceRequest> serviceRequests;


}
