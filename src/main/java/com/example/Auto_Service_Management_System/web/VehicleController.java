package com.example.Auto_Service_Management_System.web;

import com.example.Auto_Service_Management_System.model.Vehicle;
import com.example.Auto_Service_Management_System.service.CustomerService;
import com.example.Auto_Service_Management_System.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class VehicleController {

    private final CustomerService customerService;
    private final VehicleService vehicleService;

    public VehicleController(CustomerService customerService, VehicleService vehicleService) {
        this.customerService = customerService;
        this.vehicleService = vehicleService;
    }

    @GetMapping("/my_vehicles")
    public String getMyVehiclesPage(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String email = authentication.getName();
        List<Vehicle> vehicles = customerService.getVehiclesByEmail(email);

        Map<UUID, Integer> requestCounts = new HashMap<>();
        for (Vehicle v : vehicles) {
            requestCounts.put(v.getId(), vehicleService.getRequestCountForVehicle(v.getId()));
        }

        model.addAttribute("vehicles", vehicles);
        model.addAttribute("requestCounts", requestCounts);

        return "my_vehicles";
    }

    @GetMapping("/add_vehicle")
    public String showAddVehicleForm(Model model) {
        model.addAttribute("vehicle", new Vehicle());
        return "add_vehicle";
    }

    @PostMapping("/add_vehicle")
    public String addVehicle(
            @Valid @ModelAttribute("vehicle") Vehicle vehicle,
            BindingResult bindingResult,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "add_vehicle";
        }

        String email = authentication.getName();
        vehicleService.addVehicleToCustomer(vehicle, email);

        redirectAttributes.addFlashAttribute("successMessage", "Vehicle added successfully!");
        return "redirect:/my_vehicles";
    }
}
