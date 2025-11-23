package com.example.Auto_Service_Management_System.web;

import com.example.Auto_Service_Management_System.model.Customer;
import com.example.Auto_Service_Management_System.repository.CustomerRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final CustomerRepository customerRepository;

    public GlobalControllerAdvice(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @ModelAttribute("loggedUserName")
    public String loggedUserName(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            Customer customer = customerRepository.findByEmail(email).orElse(null);
            if (customer != null) {
                return customer.getFirstName();
            }
        }
        return null;
    }
}
