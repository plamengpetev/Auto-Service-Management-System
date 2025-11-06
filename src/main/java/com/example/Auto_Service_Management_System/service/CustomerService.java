package com.example.Auto_Service_Management_System.service;

import com.example.Auto_Service_Management_System.model.Customer;
import com.example.Auto_Service_Management_System.model.Role;
import com.example.Auto_Service_Management_System.repository.CustomerRepository;
import com.example.Auto_Service_Management_System.repository.RoleRepository;
import com.example.Auto_Service_Management_System.web.dto.LoginRequest;
import com.example.Auto_Service_Management_System.web.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class CustomerService {

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
        if (customerRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Default role not found"));

        Customer customer = Customer.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .roles(Set.of(userRole))
                .build();

        customerRepository.save(customer);

}





    public Customer login(@Valid LoginRequest request) {
        Optional<Customer> optionalCustomer = customerRepository.findByEmail(request.getEmail());

        if (optionalCustomer.isEmpty()) {
            throw new RuntimeException("Потребител с този имейл не съществува.");
        }

        Customer customer = optionalCustomer.get();

        // Проверка на паролата
        if (!passwordEncoder.matches(request.getPassword(), customer.getPassword())) {
            throw new RuntimeException("Грешен имейл или парола.");
        }

        return customer;
    }
}
