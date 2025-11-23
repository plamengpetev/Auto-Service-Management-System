package com.example.Auto_Service_Management_System.web;

import com.example.Auto_Service_Management_System.model.Customer;
import com.example.Auto_Service_Management_System.service.CustomerService;
import com.example.Auto_Service_Management_System.service.ServiceRequestService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final CustomerService customerService;
    private final ServiceRequestService requestService;

    public HomeController(CustomerService customerService, ServiceRequestService requestService) {
        this.customerService = customerService;
        this.requestService = requestService;
    }

    @GetMapping("/home")
    public String showHomePage(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String email = authentication.getName();
        Customer customer = customerService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        long vehicleCount = customerService.getVehiclesByEmail(email).size();
        long activeRequests = requestService.countRequestsByStatus(email, "PENDING");
        long inProgressRequests = requestService.countRequestsByStatus(email, "IN_PROGRESS");

        model.addAttribute("username", customer.getFirstName());
        model.addAttribute("vehicleCount", vehicleCount);
        model.addAttribute("activeRequests", activeRequests);
        model.addAttribute("inProgressRequests", inProgressRequests);

        return "home";
    }
}
