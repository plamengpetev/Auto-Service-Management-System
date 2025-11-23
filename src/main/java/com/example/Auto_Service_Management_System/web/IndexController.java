package com.example.Auto_Service_Management_System.web;

import com.example.Auto_Service_Management_System.service.CustomerService;
import com.example.Auto_Service_Management_System.service.ServiceRequestService;
import com.example.Auto_Service_Management_System.web.dto.LoginRequest;
import com.example.Auto_Service_Management_System.web.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class IndexController {

    private final CustomerService customerService;
    private final ServiceRequestService serviceRequestService;

    @Autowired
    public IndexController(CustomerService customerService, ServiceRequestService serviceRequestService) {
        this.customerService = customerService;
        this.serviceRequestService = serviceRequestService;
    }

    @GetMapping("/")
    public String indexRedirect(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/home";
        }
        return "index";
    }

    @GetMapping("/register")
    public ModelAndView getRegisterPage() {
        ModelAndView modelAndView = new ModelAndView("register");
        modelAndView.addObject("registerRequest", new RegisterRequest());
        return modelAndView;
    }

    @PostMapping("/register")
    public ModelAndView register(@Valid RegisterRequest registerRequest,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("register");
        }

        customerService.register(registerRequest);
        redirectAttributes.addFlashAttribute("successfulRegistration", "Registration successful!");
        return new ModelAndView("redirect:/login");
    }

    @GetMapping("/contact")
    public String getContactPage() {
        return "contact";
    }

    @GetMapping("/login")
    public ModelAndView getLoginPage() {
        ModelAndView mav = new ModelAndView("login");
        mav.addObject("loginRequest", new LoginRequest());
        return mav;
    }

    @ModelAttribute
    public void addLoggedUser(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null &&
                authentication.isAuthenticated() &&
                !"anonymousUser".equals(authentication.getPrincipal())) {

            String username = authentication.getName();
            model.addAttribute("loggedUserEmail", username);
        }
    }
}
