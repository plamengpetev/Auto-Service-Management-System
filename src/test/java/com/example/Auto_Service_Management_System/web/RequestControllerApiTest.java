package com.example.Auto_Service_Management_System.web;

import com.example.Auto_Service_Management_System.model.ServiceRequest;
import com.example.Auto_Service_Management_System.service.CustomerService;
import com.example.Auto_Service_Management_System.service.ServiceRequestService;
import com.example.Auto_Service_Management_System.web.advice.GlobalExceptionHandler;
import com.example.Auto_Service_Management_System.web.advice.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class RequestControllerApiTest {

    private MockMvc mockMvc;

    private ServiceRequestService requestService;
    private CustomerService customerService;

    private Authentication authentication;

    @BeforeEach
    void setup() {
        requestService = Mockito.mock(ServiceRequestService.class);
        customerService = Mockito.mock(CustomerService.class);
        authentication = Mockito.mock(Authentication.class);

        mockMvc = MockMvcBuilders
                .standaloneSetup(new RequestController(requestService, customerService))
                .setControllerAdvice(new GlobalExceptionHandler())   // <<< КРИТИЧНО!
                .build();
    }

    @Test
    void testGetMyRequests() throws Exception {

        when(authentication.getName()).thenReturn("test@test.com");
        when(requestService.getRequestsByEmail("test@test.com"))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/requests")
                        .principal(authentication)
                        .contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(view().name("my_requests"))
                .andExpect(model().attributeExists("requests"));
    }


    @Test
    void testViewRequestDetails_notFound() throws Exception {
        UUID id = UUID.randomUUID();

        when(requestService.getRequest(id))
                .thenThrow(new NotFoundException("Request not found"));

        mockMvc.perform(get("/requests/details/" + id)
                        .with(user("test@test.com")))
                .andExpect(status().isNotFound())       // <<< Вече минава
                .andExpect(view().name("error"))        // <<< Хендлърът връща "error"
                .andExpect(model().attribute("status", 404))
                .andExpect(model().attribute("message", "Request not found"));
    }
}
