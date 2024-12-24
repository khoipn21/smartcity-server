package com.example.city.controller;

import com.example.city.model.dto.response.ServiceResponse;
import com.example.city.service.ServiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/allservices")
@Tag(name = "Service Management", description = "APIs for managing services")
public class AllServicesController {

    private final ServiceService serviceService;

    @Autowired
    public AllServicesController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @GetMapping
    @Operation(summary = "Get All Services", description = "Retrieves a list of all existing services.")
    public ResponseEntity<List<ServiceResponse>> getAllServices() {
        List<ServiceResponse> services = serviceService.getAllServices();
        return ResponseEntity.ok(services);
    }
}