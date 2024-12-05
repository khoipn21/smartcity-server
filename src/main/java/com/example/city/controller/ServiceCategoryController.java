package com.example.city.controller;

import com.example.city.model.dto.request.ServiceCategoryRequest;
import com.example.city.model.dto.response.ServiceCategoryResponse;
import com.example.city.service.ServiceCategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/service-categories")
public class ServiceCategoryController {
    private final ServiceCategoryService serviceCategoryService;

    @Autowired
    public ServiceCategoryController(ServiceCategoryService serviceCategoryService) {
        this.serviceCategoryService = serviceCategoryService;
    }

    // Create service category
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServiceCategoryResponse> createServiceCategory(@RequestBody @Valid ServiceCategoryRequest serviceCategoryRequest) {
        ServiceCategoryResponse createdCategory = serviceCategoryService.createServiceCategory(serviceCategoryRequest);
        return ResponseEntity.ok(createdCategory);
    }

    // Get all service categories
    @GetMapping
    public ResponseEntity<List<ServiceCategoryResponse>> getAllServiceCategories() {
        List<ServiceCategoryResponse> categories = serviceCategoryService.getAllServiceCategories();
        return ResponseEntity.ok(categories);
    }

    // Get service category by ID
    @GetMapping("/{categoryId}")
    public ResponseEntity<ServiceCategoryResponse> getCityById(@PathVariable Long categoryId) {
        ServiceCategoryResponse category = serviceCategoryService.getServiceCategoryById(categoryId);
        return ResponseEntity.ok(category);
    }

    // Update service category
    @PutMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServiceCategoryResponse> updateServiceCategory(
            @PathVariable Long categoryId,
            @RequestBody @Valid ServiceCategoryRequest serviceCategoryRequest) {
        ServiceCategoryResponse updatedCategory = serviceCategoryService.updateServiceCategory(categoryId,
                serviceCategoryRequest);
        return ResponseEntity.ok(updatedCategory);
    }

    // Delete service category
    @DeleteMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteServiceCategory(@PathVariable Long categoryId) {
        serviceCategoryService.deleteServiceCategory(categoryId);
        return ResponseEntity.ok("Service category deleted successfully");
    }
}
