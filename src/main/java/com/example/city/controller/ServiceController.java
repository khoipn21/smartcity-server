package com.example.city.controller;

import com.example.city.model.dto.request.ServiceRequest;
import com.example.city.model.dto.response.DetailServiceResponse;
import com.example.city.model.dto.response.ServiceResponse;
import com.example.city.service.ServiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/cities/{cityId}/services")
@Tag(name = "Service", description = "APIs for managing services within a city")
public class ServiceController {

    private final ServiceService serviceService;

    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new service with images", description = "Adds a new service with one or multiple images to a specific city. Only admins can perform this action.")
    public ResponseEntity<ServiceResponse> createService(
            @PathVariable Long cityId,
            @ModelAttribute @Valid ServiceRequest serviceRequest,
            @RequestParam(value = "images", required = false) MultipartFile[] images) {
        ServiceResponse createdService = serviceService.createService(cityId, serviceRequest, images);
        return ResponseEntity.ok(createdService);
    }

    @GetMapping
    @Operation(summary = "Get all services in a city", description = "Retrieves all services available in a specific city.")
    public ResponseEntity<List<ServiceResponse>> getAllServicesInCity(@PathVariable Long cityId) {
        List<ServiceResponse> services = serviceService.getAllServicesInCity(cityId);
        return ResponseEntity.ok(services);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get service details by ID", description = "Retrieves detailed information about a specific service.")
    public ResponseEntity<DetailServiceResponse> getServiceById(@PathVariable Long id) {
        DetailServiceResponse service = serviceService.getServiceById(id);
        return ResponseEntity.ok(service);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update an existing service with images", description = "Updates the details and images of an existing service by its ID. Only admins can perform this action.")
    public ResponseEntity<ServiceResponse> updateService(
            @PathVariable Long id,
            @ModelAttribute @Valid ServiceRequest serviceRequest,
            @RequestParam(value = "images", required = false) MultipartFile[] images) {
        ServiceResponse updatedService = serviceService.updateService(id, serviceRequest, images);
        return ResponseEntity.ok(updatedService);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a service", description = "Deletes a service by its ID. Only admins can perform this action.")
    public ResponseEntity<String> deleteService(@PathVariable Long id) {
        serviceService.deleteService(id);
        return ResponseEntity.ok("Service deleted successfully");
    }

}