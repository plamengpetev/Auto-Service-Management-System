package com.example.Auto_Service_Management_System.web.advice;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    // --------------------------------------------------------------
    // CUSTOM EXCEPTION (your NotFoundException)
    // --------------------------------------------------------------
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(NotFoundException ex,
                                 HttpServletRequest request,
                                 Model model,
                                 RedirectAttributes redirect) {

        String uri = request.getRequestURI();

        // ADMIN area → redirect
        if (uri.startsWith("/admin")) {
            redirect.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/dashboard";
        }

        // Normal users → error page
        model.addAttribute("status", 404);
        model.addAttribute("error", "Resource not found");
        model.addAttribute("message", ex.getMessage());
        return "error";
    }

    // --------------------------------------------------------------
    // BUILT-IN EXCEPTION: IllegalArgumentException
    // --------------------------------------------------------------
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgument(IllegalArgumentException ex, Model model) {
        model.addAttribute("status", 400);
        model.addAttribute("error", "Invalid request");
        model.addAttribute("message", ex.getMessage());
        return "error";
    }

    // --------------------------------------------------------------
    // BUILT-IN EXCEPTION: IllegalStateException
    // (Used for mechanic availability, auto-assign errors, bad status etc.)
    // --------------------------------------------------------------
    @ExceptionHandler(IllegalStateException.class)
    public String handleIllegalState(IllegalStateException ex,
                                     HttpServletRequest request,
                                     RedirectAttributes redirect,
                                     Model model) {

        String uri = request.getRequestURI();

        // Admin area requires redirect (important for POST actions)
        if (uri.startsWith("/admin")) {
            redirect.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/dashboard";
        }

        // Normal user → show error page
        model.addAttribute("status", 400);
        model.addAttribute("error", "Invalid operation");
        model.addAttribute("message", ex.getMessage());
        return "error";
    }

    // --------------------------------------------------------------
    // BUILT-IN EXCEPTION: AccessDeniedException
    // (Spring Security)
    // --------------------------------------------------------------
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAccessDenied(AccessDeniedException ex, Model model) {
        model.addAttribute("status", 403);
        model.addAttribute("error", "Access denied");
        model.addAttribute("message", "You do not have permission to access this page.");
        return "error";
    }

    // --------------------------------------------------------------
    // FALLBACK EXCEPTION
    // --------------------------------------------------------------
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGeneral(Exception ex, Model model) {
        model.addAttribute("status", 500);
        model.addAttribute("error", "Internal server error");
        model.addAttribute("message", "An unexpected error occurred. Please try again.");
        return "error";
    }
}
