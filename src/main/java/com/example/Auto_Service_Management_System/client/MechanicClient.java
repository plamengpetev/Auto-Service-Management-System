package com.example.Auto_Service_Management_System.client;

import com.example.Auto_Service_Management_System.web.dto.MechanicDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "mechanic-service", url = "http://localhost:8081/api/mechanics")
public interface MechanicClient {

    @GetMapping
    List<MechanicDTO> getAllMechanics();

    @GetMapping("/{id}")
    MechanicDTO getMechanic(@PathVariable UUID id);

    @PostMapping("/assign")
    MechanicDTO assignMechanic();

    @PutMapping("/{id}/availability")
    void updateAvailability(@PathVariable UUID id, @RequestBody Map<String, Boolean> body);
}
