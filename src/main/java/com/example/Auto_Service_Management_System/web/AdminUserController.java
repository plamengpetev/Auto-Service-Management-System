package com.example.Auto_Service_Management_System.web;

import com.example.Auto_Service_Management_System.service.RoleService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private final RoleService roleService;

    public AdminUserController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/make-admin/{email}")
    public String makeAdmin(@PathVariable String email, RedirectAttributes redirect) {
        roleService.makeAdmin(email);
        redirect.addFlashAttribute("successMessage", "User is now an ADMIN!");
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/make-user/{email}")
    public String makeUser(@PathVariable String email, RedirectAttributes redirect) {
        roleService.makeUser(email);
        redirect.addFlashAttribute("successMessage", "User is now a USER!");
        return "redirect:/admin/dashboard";
    }
}
