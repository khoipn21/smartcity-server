package com.example.city.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceRequest {
    @NotBlank(message = "Service name is required")
    @Size(max = 100, message = "Service name must be less than 100 characters")
    private String name;

    @Size(max = 500, message = "Description must be less than 500 characters")
    private String description;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Contact information is required")
    private String contactInfo;

    @Size(max = 1000, message = "Operating hours must be less than 1000 characters")
    private String operatingHours;

    @NotNull(message = "Service category ID is required")
    private Long categoryId;
}