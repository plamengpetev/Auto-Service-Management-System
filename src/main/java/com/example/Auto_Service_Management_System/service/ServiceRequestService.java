package com.example.Auto_Service_Management_System.service;

import com.example.Auto_Service_Management_System.client.MechanicClient;
import com.example.Auto_Service_Management_System.model.RequestStatus;
import com.example.Auto_Service_Management_System.model.ServiceRequest;
import com.example.Auto_Service_Management_System.model.Customer;
import com.example.Auto_Service_Management_System.model.Vehicle;
import com.example.Auto_Service_Management_System.repository.ServiceRequestRepository;
import com.example.Auto_Service_Management_System.repository.CustomerRepository;
import com.example.Auto_Service_Management_System.repository.VehicleRepository;
import com.example.Auto_Service_Management_System.web.advice.NotFoundException;
import com.example.Auto_Service_Management_System.web.dto.MechanicDTO;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ServiceRequestService {

    private static final Logger logger = LoggerFactory.getLogger(ServiceRequestService.class);

    private final ServiceRequestRepository requestRepository;
    private final CustomerRepository customerRepository;
    private final VehicleRepository vehicleRepository;
    private final MechanicClient mechanicClient;

    public ServiceRequestService(ServiceRequestRepository requestRepository,
                                 CustomerRepository customerRepository,
                                 VehicleRepository vehicleRepository,
                                 MechanicClient mechanicClient) {
        this.requestRepository = requestRepository;
        this.customerRepository = customerRepository;
        this.vehicleRepository = vehicleRepository;
        this.mechanicClient = mechanicClient;
    }

    // ------------------------------------------------------------
    // CREATE REQUEST
    // ------------------------------------------------------------
    @CacheEvict(value = "requestsByEmail", key = "#email")
    public void createRequest(String email, UUID vehicleId, String description) {
        logger.info("Creating request for [{}], vehicle [{}]", email, vehicleId);

        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new NotFoundException("Vehicle not found"));

        ServiceRequest request = ServiceRequest.builder()
                .description(description)
                .status(RequestStatus.PENDING)
                .customer(customer)
                .vehicle(vehicle)
                .requestDate(LocalDateTime.now())
                .build();

        requestRepository.save(request);

        logger.info("Request [{}] created successfully", request.getId());
    }

    // ------------------------------------------------------------
    // FETCH REQUESTS BY EMAIL (CACHED)
    // ------------------------------------------------------------
    @Cacheable(value = "requestsByEmail", key = "#email")
    public List<ServiceRequest> getRequestsByEmail(String email) {
        logger.info("Fetching service requests for [{}]", email);
        return requestRepository.findByCustomerEmail(email);
    }

    // ------------------------------------------------------------
    // FETCH SINGLE REQUEST
    // ------------------------------------------------------------
    public ServiceRequest getRequest(UUID id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Request not found"));
    }

    // ------------------------------------------------------------
    // CANCEL REQUEST
    // ------------------------------------------------------------
    @CacheEvict(value = "requestsByEmail", key = "#userEmail")
    public void cancel(UUID requestId, String userEmail) {
        ServiceRequest request = getRequest(requestId);

        if (!request.getCustomer().getEmail().equals(userEmail)) {
            throw new NotFoundException("Unauthorized");
        }

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new NotFoundException("Request cannot be cancelled");
        }

        request.setStatus(RequestStatus.CANCELLED);
        requestRepository.save(request);

        logger.info("Request [{}] cancelled by {}", requestId, userEmail);
    }

    // ------------------------------------------------------------
    // AUTO ASSIGN MECHANIC (POST to microservice)
    // ------------------------------------------------------------
    @Transactional
    @CacheEvict(value = "requestsByEmail", allEntries = true)
    public void autoAssignMechanic(UUID requestId) {

        ServiceRequest request = getRequest(requestId);

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Mechanic can be auto-assigned only to PENDING requests");
        }

        MechanicDTO mechanic;

        try {
            mechanic = mechanicClient.assignMechanic();
        } catch (FeignException.NotFound ex) {
            throw new IllegalStateException("No available mechanics");
        } catch (FeignException ex) {
            throw new IllegalStateException("Mechanic service unavailable");
        }

        request.setMechanicId(mechanic.getId());
        request.setMechanicName(mechanic.getName());
        request.setMechanicSpecialization(mechanic.getSpecialization());
        request.setStatus(RequestStatus.IN_PROGRESS);

        requestRepository.save(request);

        logger.info("AUTO Assigned mechanic [{}] to request [{}]", mechanic.getId(), requestId);
    }

    // ------------------------------------------------------------
    // MANUAL ASSIGN (via dropdown)
    // ------------------------------------------------------------
    @Transactional
    @CacheEvict(value = "requestsByEmail", allEntries = true)
    public void assignSpecificMechanic(UUID requestId, UUID mechanicId) {

        ServiceRequest request = getRequest(requestId);

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Manual assignment allowed only for PENDING requests");
        }

        MechanicDTO mechanic = mechanicClient.getMechanic(mechanicId);

        if (!mechanic.isAvailable()) {
            throw new IllegalStateException("Selected mechanic is not available");
        }

        mechanicClient.updateAvailability(mechanicId, Map.of("available", false));

        request.setMechanicId(mechanic.getId());
        request.setMechanicName(mechanic.getName());
        request.setMechanicSpecialization(mechanic.getSpecialization());
        request.setStatus(RequestStatus.IN_PROGRESS);

        requestRepository.save(request);

        logger.info("MANUAL Assigned mechanic [{}] to request [{}]", mechanicId, requestId);
    }

    // ------------------------------------------------------------
    // RELEASE MECHANIC
    // ------------------------------------------------------------
    @Transactional
    @CacheEvict(value = "requestsByEmail", allEntries = true)
    public void releaseMechanic(UUID requestId) {

        ServiceRequest request = getRequest(requestId);

        if (request.getMechanicId() == null) {
            throw new IllegalStateException("This request has no assigned mechanic");
        }

        UUID mechanicId = UUID.fromString(request.getMechanicId());

        mechanicClient.updateAvailability(mechanicId, Map.of("available", true));

        request.setStatus(RequestStatus.COMPLETED);
        requestRepository.save(request);

        logger.info("Released mechanic [{}] from request [{}]", mechanicId, requestId);
    }

    // ------------------------------------------------------------
    // COUNT REQUESTS
    // ------------------------------------------------------------
    public long countRequestsByStatus(String email, String status) {
        RequestStatus s = RequestStatus.valueOf(status);
        return requestRepository.countByCustomerEmailAndStatus(email, s);
    }
}
