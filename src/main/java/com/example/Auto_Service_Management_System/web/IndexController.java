package com.example.Auto_Service_Management_System.web;

import com.example.Auto_Service_Management_System.service.CustomerService;
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

    @Autowired
    public IndexController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/")
    public String getIndexController() {
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
        redirectAttributes.addFlashAttribute("successfulRegistration", "Регистрацията е успешна!");
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

    @GetMapping("/home")
    public String homePage(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String username = authentication.getName(); // това е email-ът
        model.addAttribute("username", username);

        return "home";
    }

    @ModelAttribute
    public void addLoggedUser(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                !authentication.getPrincipal().equals("anonymousUser")) {
            String username = authentication.getName(); // това е email-а на потребителя
            model.addAttribute("loggedUserEmail", username);
        }
    }


}
