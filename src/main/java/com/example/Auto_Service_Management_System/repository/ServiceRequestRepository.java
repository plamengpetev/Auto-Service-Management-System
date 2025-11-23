package com.example.Auto_Service_Management_System.repository;

import com.example.Auto_Service_Management_System.model.RequestStatus;
import com.example.Auto_Service_Management_System.model.ServiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, UUID> {

    List<ServiceRequest> findByCustomerEmail(String email);

    long countByCustomerEmailAndStatus(String email, RequestStatus status);

    List<ServiceRequest> findByStatusAndRequestDateBefore(RequestStatus status, LocalDate date);

    long countByVehicleId(UUID vehicleId);
}
