package com.example.city.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceResponse {
    private Long id;
    private Long categoryId;
    private String categoryName;
    private Long cityId;
    private String cityName;
    private String name;
    private String description;
    private String address;
    private String contactInfo;
    private String operatingHours;
    private Instant createdAt;
    private Instant updatedAt;
}