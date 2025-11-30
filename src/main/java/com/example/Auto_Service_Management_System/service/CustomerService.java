package com.example.Auto_Service_Management_System.service;

import com.example.Auto_Service_Management_System.model.Customer;
import com.example.Auto_Service_Management_System.model.Role;
import com.example.Auto_Service_Management_System.model.Vehicle;
import com.example.Auto_Service_Management_System.repository.CustomerRepository;
import com.example.Auto_Service_Management_System.repository.RoleRepository;
import com.example.Auto_Service_Management_System.web.advice.NotFoundException;
import com.example.Auto_Service_Management_System.web.dto.LoginRequest;
import com.example.Auto_Service_Management_System.web.dto.ProfileUpdateRequest;
import com.example.Auto_Service_Management_System.web.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    private final CustomerRepository customerRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public CustomerService(CustomerRepository customerRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(RegisterRequest request) {
        logger.info("Attempting registration for email [{}]", request.getEmail());

        if (customerRepository.findByEmail(request.getEmail()).isPresent()) {
            logger.warn("Registration failed — user [{}] already exists", request.getEmail());
            throw new NotFoundException("User already exists");
        }

        boolean isFirstUser = customerRepository.count() == 0;

        Role role = roleRepository.findByName(isFirstUser ? "ROLE_ADMIN" : "ROLE_USER")
                .orElseThrow(() -> {
                    logger.error("Registration failed — default role not found");
                    return new NotFoundException("Default role not found");
                });

        Customer customer = Customer.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .roles(Set.of(role))
                .build();

        customerRepository.save(customer);

        logger.info("User [{}] registered successfully with role [{}]",
                request.getEmail(),
                role.getName());
    }

    public Customer login(@Valid LoginRequest request) {
        logger.info("Login attempt for email [{}]", request.getEmail());

        Customer customer = customerRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    logger.warn("Login failed — email [{}] not found", request.getEmail());
                    return new NotFoundException("A user with this email does not exist.");
                });

        if (!passwordEncoder.matches(request.getPassword(), customer.getPassword())) {
            logger.warn("Login failed — incorrect password for [{}]", request.getEmail());
            throw new NotFoundException("Invalid email or password.");
        }

        logger.info("User [{}] logged in successfully", request.getEmail());
        return customer;
    }

    @Cacheable(value = "vehiclesByEmail", key = "#email")
    public List<Vehicle> getVehiclesByEmail(String email) {
        logger.info("Fetching vehicles for user [{}] (cached/non-cached)", email);
        return customerRepository.findByEmail(email)
                .map(Customer::getVehicles)
                .orElse(List.of());
    }

    public Optional<Customer> findByEmail(String email) {
        logger.info("Searching customer by email [{}]", email);
        return customerRepository.findByEmail(email);
    }

    public Customer getProfileByEmail(String email) {
        logger.info("Loading profile for user [{}]", email);

        return customerRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("Profile for email [{}] not found", email);
                    return new NotFoundException("User not found");
                });
    }

    public void updateProfile(String email, ProfileUpdateRequest request) {
        logger.info("Updating profile for user [{}]", email);

        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("Profile update failed — user with email [{}] not found", email);
                    return new NotFoundException("User not found");
                });

        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setPhoneNumber(request.getPhoneNumber());

        customerRepository.save(customer);

        logger.info("Profile for user [{}] successfully updated", email);
    }

}
