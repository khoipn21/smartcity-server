package com.example.city.controller;

import com.example.city.model.dto.request.ServiceCategoryRequest;
import com.example.city.model.dto.response.ServiceCategoryResponse;
import com.example.city.service.ServiceCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/service-categories")
@Tag(name = "Service Category Management", description = "APIs for managing service categories")
public class ServiceCategoryController {
    private final ServiceCategoryService serviceCategoryService;

    @Autowired
    public ServiceCategoryController(ServiceCategoryService serviceCategoryService) {
        this.serviceCategoryService = serviceCategoryService;
    }

    /**
     * Creates a new service category.
     *
     * @param serviceCategoryRequest The request payload containing service category details.
     * @return The created service category response.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create Service Category", description = "Creates a new service category. Only admins can perform this action.")
    public ResponseEntity<ServiceCategoryResponse> createServiceCategory(@RequestBody @Valid ServiceCategoryRequest serviceCategoryRequest) {
        ServiceCategoryResponse createdCategory = serviceCategoryService.createServiceCategory(serviceCategoryRequest);
        return ResponseEntity.ok(createdCategory);
    }

    /**
     * Retrieves all service categories.
     *
     * @return A list of all service category responses.
     */
    @GetMapping
    @Operation(summary = "Get All Service Categories", description = "Retrieves all service categories.")
    public ResponseEntity<List<ServiceCategoryResponse>> getAllServiceCategories() {
        List<ServiceCategoryResponse> categories = serviceCategoryService.getAllServiceCategories();
        return ResponseEntity.ok(categories);
    }

    /**
     * Retrieves a specific service category by its ID.
     *
     * @param categoryId The ID of the service category to retrieve.
     * @return The service category response.
     */
    @GetMapping("/{categoryId}")
    @Operation(summary = "Get Service Category by ID", description = "Retrieves a service category by its ID.")
    public ResponseEntity<ServiceCategoryResponse> getServiceCategoryById(@PathVariable Long categoryId) {
        ServiceCategoryResponse category = serviceCategoryService.getServiceCategoryById(categoryId);
        return ResponseEntity.ok(category);
    }

    /**
     * Updates an existing service category.
     *
     * @param categoryId             The ID of the service category to update.
     * @param serviceCategoryRequest The request payload containing updated service category details.
     * @return The updated service category response.
     */
    @PutMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update Service Category", description = "Updates an existing service category by its ID. Only admins can perform this action.")
    public ResponseEntity<ServiceCategoryResponse> updateServiceCategory(
            @PathVariable Long categoryId,
            @RequestBody @Valid ServiceCategoryRequest serviceCategoryRequest) {
        ServiceCategoryResponse updatedCategory = serviceCategoryService.updateServiceCategory(categoryId, serviceCategoryRequest);
        return ResponseEntity.ok(updatedCategory);
    }

    /**
     * Deletes a service category.
     *
     * @param categoryId The ID of the service category to delete.
     * @return A confirmation message upon successful deletion.
     */
    @DeleteMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete Service Category", description = "Deletes a service category by its ID. Only admins can perform this action.")
    public ResponseEntity<String> deleteServiceCategory(@PathVariable Long categoryId) {
        serviceCategoryService.deleteServiceCategory(categoryId);
        return ResponseEntity.ok("Service category deleted successfully");
    }
}