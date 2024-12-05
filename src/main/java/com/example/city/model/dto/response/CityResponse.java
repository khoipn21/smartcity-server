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
public class CityResponse {
    private Long id;
    private String name;
    private String country;
    private String description;
    private Instant createdAt;
}
