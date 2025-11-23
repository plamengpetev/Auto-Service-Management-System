package com.example.Auto_Service_Management_System.service;

import com.example.Auto_Service_Management_System.model.Customer;
import com.example.Auto_Service_Management_System.model.Vehicle;
import com.example.Auto_Service_Management_System.repository.CustomerRepository;
import com.example.Auto_Service_Management_System.repository.ServiceRequestRepository;
import com.example.Auto_Service_Management_System.repository.VehicleRepository;
import com.example.Auto_Service_Management_System.web.advice.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class VehicleService {

    private static final Logger logger = LoggerFactory.getLogger(VehicleService.class);

    private final VehicleRepository vehicleRepository;
    private final CustomerRepository customerRepository;
    private final ServiceRequestRepository serviceRequestRepository;

    public VehicleService(VehicleRepository vehicleRepository,
                          CustomerRepository customerRepository,
                          ServiceRequestRepository serviceRequestRepository) {
        this.vehicleRepository = vehicleRepository;
        this.customerRepository = customerRepository;
        this.serviceRequestRepository = serviceRequestRepository;
    }

    @CacheEvict(value = "vehiclesByEmail", key = "#userIdentifier")
    public void addVehicleToCustomer(Vehicle vehicle, String userIdentifier) {
        logger.info("Attempting to add vehicle to user [{}]", userIdentifier);

        Customer customer = findCustomer(userIdentifier);

        vehicle.setOwner(customer);
        vehicleRepository.save(vehicle);

        logger.info("Vehicle [{}] successfully added to user [{}]", vehicle.getId(), userIdentifier);
    }

    public int getRequestCountForVehicle(UUID vehicleId) {
        int count = (int) serviceRequestRepository.countByVehicleId(vehicleId);
        logger.info("Vehicle [{}] has [{}] service requests", vehicleId, count);
        return count;
    }

    private Customer findCustomer(String identifier) {
        logger.info("Searching customer by identifier [{}]", identifier);

        return customerRepository.findByEmail(identifier)
                .orElseGet(() -> customerRepository.findByUsername(identifier)
                        .orElseThrow(() -> {
                            logger.warn("Customer [{}] not found", identifier);
                            return new NotFoundException("Customer not found: " + identifier);
                        }));
    }
}
