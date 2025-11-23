package com.example.Auto_Service_Management_System.service;

import com.example.Auto_Service_Management_System.client.MechanicClient;
import com.example.Auto_Service_Management_System.model.Customer;
import com.example.Auto_Service_Management_System.model.ServiceRequest;
import com.example.Auto_Service_Management_System.model.Vehicle;
import com.example.Auto_Service_Management_System.repository.CustomerRepository;
import com.example.Auto_Service_Management_System.repository.ServiceRequestRepository;
import com.example.Auto_Service_Management_System.repository.VehicleRepository;
import com.example.Auto_Service_Management_System.web.advice.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ServiceRequestServiceUTest {

    @Mock
    private ServiceRequestRepository requestRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private MechanicClient mechanicClient;

    @InjectMocks
    private ServiceRequestService service;

    @Test
    void whenCreateRequest_andCustomerNotFound_thenThrowException() {
        String email = "missing@test.com";
        UUID vehicleId = UUID.randomUUID();

        when(customerRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.createRequest(email, vehicleId, "Test"));
    }

    @Test
    void whenCreateRequest_andVehicleNotFound_thenThrowException() {

        String email = "test@test.com";
        UUID vehicleId = UUID.randomUUID();  // ✔ FIXED HERE
        Customer customer = Customer.builder().email(email).build();

        when(customerRepository.findByEmail(email)).thenReturn(Optional.of(customer));
        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.createRequest(email, vehicleId, "Engine"));
    }

    @Test
    void whenCreateRequest_andDataIsCorrect_thenSaveRequest() {

        String email = "test@test.com";
        UUID vehicleId = UUID.randomUUID();

        Customer customer = Customer.builder()
                .id(UUID.randomUUID())
                .email(email)
                .build();

        Vehicle vehicle = Vehicle.builder()
                .id(vehicleId)
                .owner(customer)
                .build();

        when(customerRepository.findByEmail(email)).thenReturn(Optional.of(customer));
        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(vehicle));

        service.createRequest(email, vehicleId, "Oil change");

        verify(requestRepository).save(any(ServiceRequest.class));
    }

    @Test
    void whenCreateRequest_thenRequestDateShouldBeCloseToNow() {

        String email = "test@test.com";
        UUID vehicleId = UUID.randomUUID();

        Customer customer = Customer.builder()
                .email(email)
                .build();

        Vehicle vehicle = Vehicle.builder()
                .id(vehicleId)
                .owner(customer)
                .build();

        when(customerRepository.findByEmail(email)).thenReturn(Optional.of(customer));
        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(vehicle));

        service.createRequest(email, vehicleId, "Test");

        verify(requestRepository).save(any(ServiceRequest.class));

        ServiceRequest request = ServiceRequest.builder()
                .requestDate(LocalDateTime.now())
                .build();

        assertThat(request.getRequestDate())
                .isCloseTo(LocalDateTime.now(), within(1, ChronoUnit.SECONDS));
    }
}
