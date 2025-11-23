package com.example.Auto_Service_Management_System.web;

import com.example.Auto_Service_Management_System.model.Customer;
import com.example.Auto_Service_Management_System.service.CustomerService;
import com.example.Auto_Service_Management_System.web.dto.ProfileUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileController {

    private final CustomerService customerService;

    public ProfileController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/profile")
    public String showProfile(Authentication authentication, Model model) {
        String email = authentication.getName();
        Customer customer = customerService.getProfileByEmail(email);

        ProfileUpdateRequest dto = ProfileUpdateRequest.builder()
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .phoneNumber(customer.getPhoneNumber())
                .build();

        if (!model.containsAttribute("profileForm")) {
            model.addAttribute("profileForm", dto);
        }

        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(
            Authentication authentication,
            @Valid @ModelAttribute("profileForm") ProfileUpdateRequest profileForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.profileForm", bindingResult);
            redirectAttributes.addFlashAttribute("profileForm", profileForm);
            return "redirect:/profile";
        }

        String email = authentication.getName();
        customerService.updateProfile(email, profileForm);

        redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully");
        return "redirect:/profile";
    }
}
