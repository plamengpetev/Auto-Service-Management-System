package com.example.Auto_Service_Management_System.web;

import com.example.Auto_Service_Management_System.client.MechanicClient;
import com.example.Auto_Service_Management_System.model.ServiceRequest;
import com.example.Auto_Service_Management_System.repository.CustomerRepository;
import com.example.Auto_Service_Management_System.repository.ServiceRequestRepository;
import com.example.Auto_Service_Management_System.repository.VehicleRepository;
import com.example.Auto_Service_Management_System.service.ServiceRequestService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final CustomerRepository customerRepository;
    private final VehicleRepository vehicleRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final MechanicClient mechanicClient;
    private final ServiceRequestService requestService;

    public AdminController(CustomerRepository customerRepository,
                           VehicleRepository vehicleRepository,
                           ServiceRequestRepository serviceRequestRepository,
                           MechanicClient mechanicClient,
                           ServiceRequestService requestService) {
        this.customerRepository = customerRepository;
        this.vehicleRepository = vehicleRepository;
        this.serviceRequestRepository = serviceRequestRepository;
        this.mechanicClient = mechanicClient;
        this.requestService = requestService;
    }

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        List<ServiceRequest> requests = serviceRequestRepository
                .findAll()
                .stream()
                .sorted((a, b) -> b.getRequestDate().compareTo(a.getRequestDate()))
                .toList();

        model.addAttribute("customers", customerRepository.findAll());
        model.addAttribute("vehicles", vehicleRepository.findAll());
        model.addAttribute("requests", requests);
        model.addAttribute("mechanics", mechanicClient.getAllMechanics());

        return "admin_dashboard";
    }

    @PostMapping("/assign/{id}")
    public String assignMechanicToRequest(@PathVariable UUID id,
                                          @RequestParam UUID mechanicId,
                                          RedirectAttributes redirectAttributes) {

        requestService.assignSpecificMechanic(id, mechanicId);

        redirectAttributes.addFlashAttribute("successMessage",
                "Mechanic assigned successfully!");

        return "redirect:/admin/dashboard";
    }

    @PostMapping("/assign/auto/{id}")
    public String autoAssignMechanic(@PathVariable UUID id,
                                     RedirectAttributes redirectAttributes) {

        requestService.autoAssignMechanic(id);

        redirectAttributes.addFlashAttribute("successMessage",
                "Mechanic automatically assigned successfully!");

        return "redirect:/admin/dashboard";
    }

    @PostMapping("/release/{id}")
    public String releaseMechanicFromRequest(@PathVariable UUID id,
                                             RedirectAttributes redirectAttributes) {

        requestService.releaseMechanic(id);

        redirectAttributes.addFlashAttribute("successMessage",
                "Mechanic released and request completed successfully!");

        return "redirect:/admin/dashboard";
    }
}
