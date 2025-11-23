package com.example.Auto_Service_Management_System.service;

import com.example.Auto_Service_Management_System.model.Customer;
import com.example.Auto_Service_Management_System.model.ServiceRequest;
import com.example.Auto_Service_Management_System.model.Vehicle;
import com.example.Auto_Service_Management_System.repository.CustomerRepository;
import com.example.Auto_Service_Management_System.repository.ServiceRequestRepository;
import com.example.Auto_Service_Management_System.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class ServiceRequestServiceITest {

    @Autowired
    private ServiceRequestService requestService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private ServiceRequestRepository requestRepository;

    @Test
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void testCreateRequest_integration() {

        // 1) Customer с всички required полета
        Customer customer = Customer.builder()
                .firstName("Test")
                .lastName("User")
                .email("test@integration.com")
                .username("testUser")
                .password("password123")
                .phoneNumber("111222333")
                .build();

        customer = customerRepository.save(customer);

        // 2) Vehicle
        Vehicle vehicle = Vehicle.builder()
                .brand("BMW")
                .model("330i")
                .owner(customer)
                .registrationNumber("TEST-1234")
                .year(2020)
                .build();

        vehicle = vehicleRepository.save(vehicle);


        // 3) Създаваме Service Request
        requestService.createRequest(
                customer.getEmail(),
                vehicle.getId(),
                "Brake check"
        );

        // 4) Проверка в базата
        List<ServiceRequest> requests =
                requestRepository.findByCustomerEmail(customer.getEmail());

        assertThat(requests).hasSize(1);
        assertThat(requests.get(0).getDescription()).isEqualTo("Brake check");
        assertThat(requests.get(0).getVehicle().getId()).isEqualTo(vehicle.getId());
    }
}
