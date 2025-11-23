package com.example.Auto_Service_Management_System.web;

import com.example.Auto_Service_Management_System.model.ServiceRequest;
import com.example.Auto_Service_Management_System.model.Vehicle;
import com.example.Auto_Service_Management_System.service.CustomerService;
import com.example.Auto_Service_Management_System.service.ServiceRequestService;
import com.example.Auto_Service_Management_System.web.advice.NotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/requests")
public class RequestController {

    private final ServiceRequestService requestService;
    private final CustomerService customerService;

    public RequestController(ServiceRequestService requestService, CustomerService customerService) {
        this.requestService = requestService;
        this.customerService = customerService;
    }

    @GetMapping("/new")
    public String showNewRequestForm(Model model, Authentication auth) {
        String email = auth.getName();
        List<Vehicle> vehicles = customerService.getVehiclesByEmail(email);
        model.addAttribute("vehicles", vehicles);
        return "new_request";
    }

    @PostMapping("/new")
    public String submitNewRequest(@RequestParam UUID vehicleId,
                                   @RequestParam String description,
                                   Authentication auth) {
        String email = auth.getName();
        requestService.createRequest(email, vehicleId, description);
        return "redirect:/requests";
    }

    @GetMapping
    public String viewUserRequests(Authentication auth, Model model) {
        String email = auth.getName();
        model.addAttribute("requests", requestService.getRequestsByEmail(email));
        return "my_requests";
    }

    @GetMapping("/details/{id}")
    public String viewRequestDetails(@PathVariable UUID id,
                                     Model model,
                                     Authentication auth) {

        ServiceRequest request = requestService.getRequest(id);

        if (!request.getCustomer().getEmail().equals(auth.getName())) {
            throw new NotFoundException("You do not have access to this request.");
        }

        model.addAttribute("request", request);
        return "request_details";
    }

    @PostMapping("/cancel/{id}")
    public String cancelRequest(@PathVariable UUID id, Authentication auth) {
        requestService.cancel(id, auth.getName());
        return "redirect:/requests";
    }
}
