package com.example.city.controller;

import com.example.city.model.dto.request.ServiceRequest;
import com.example.city.model.dto.response.ServiceResponse;
import com.example.city.service.ServiceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cities/{cityId}/services")
public class ServiceController {
    private final ServiceService serviceService;

    @Autowired
    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServiceResponse> createService(
            @PathVariable Long cityId,
            @RequestBody @Valid ServiceRequest serviceRequest) {
        ServiceResponse createdService = serviceService.createService(cityId, serviceRequest);
        return ResponseEntity.ok(createdService);
    }
}