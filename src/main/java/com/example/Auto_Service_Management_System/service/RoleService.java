package com.example.Auto_Service_Management_System.service;

import com.example.Auto_Service_Management_System.model.Customer;
import com.example.Auto_Service_Management_System.model.Role;
import com.example.Auto_Service_Management_System.repository.CustomerRepository;
import com.example.Auto_Service_Management_System.repository.RoleRepository;
import com.example.Auto_Service_Management_System.web.advice.NotFoundException;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    private final CustomerRepository customerRepository;
    private final RoleRepository roleRepository;

    public RoleService(CustomerRepository customerRepository, RoleRepository roleRepository) {
        this.customerRepository = customerRepository;
        this.roleRepository = roleRepository;
    }

    public void makeAdmin(String email) {
        assignRole(email, "ROLE_ADMIN");
    }

    public void makeUser(String email) {
        assignRole(email, "ROLE_USER");
    }

    private void assignRole(String email, String roleName) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found."));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new NotFoundException("Role " + roleName + " not found"));

        customer.getRoles().clear();
        customer.getRoles().add(role);

        customerRepository.save(customer);
    }
}
