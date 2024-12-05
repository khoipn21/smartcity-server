package com.example.city.controller;

import com.example.city.model.dto.request.ServiceRequest;
import com.example.city.model.dto.response.ServiceResponse;
import com.example.city.service.ServiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cities/{cityId}/services")
@Tag(name = "Service Management", description = "APIs for managing services within a city")
public class ServiceController {
    private final ServiceService serviceService;

    @Autowired
    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new service", description = "Adds a new service to a specific city. Only admins can perform this action.")
    public ResponseEntity<ServiceResponse> createService(
            @PathVariable Long cityId,
            @RequestBody @Valid ServiceRequest serviceRequest) {
        ServiceResponse createdService = serviceService.createService(cityId, serviceRequest);
        return ResponseEntity.ok(createdService);
    }

    @GetMapping
    @Operation(summary = "Get all services in a city", description = "Retrieves all services available in a specific city.")
    public ResponseEntity<List<ServiceResponse>> getAllServicesInCity(@PathVariable Long cityId) {
        List<ServiceResponse> services = serviceService.getAllServicesInCity(cityId);
        return ResponseEntity.ok(services);
    }
}