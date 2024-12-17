package com.example.city.controller;

import com.example.city.model.dto.request.CityRequest;
import com.example.city.model.dto.response.CityResponse;
import com.example.city.service.CityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cities")
@Tag(name = "City Management", description = "APIs for managing cities")
public class CityController {
    private final CityService cityService;

    @Autowired
    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping
    @Operation(summary = "Retrieve all cities", description = "Fetches a list of all cities.")
    public ResponseEntity<List<CityResponse>> getAllCities() {
        List<CityResponse> cities = cityService.getAllCities();
        return ResponseEntity.ok(cities);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retrieve a city by ID", description = "Fetches the details of a specific city by its ID.")
    public ResponseEntity<CityResponse> getCityById(@PathVariable Long id) {
        CityResponse city = cityService.getCityById(id);
        return ResponseEntity.ok(city);
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Add a new city", description = "Creates a new city. Only admins can perform this action.")
    public ResponseEntity<CityResponse> addCity(@RequestBody @Valid CityRequest cityRequest) {
        CityResponse createdCity = cityService.addCity(cityRequest);
        return ResponseEntity.ok(createdCity);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update an existing city", description = "Updates the details of an existing city by its ID. Only admins can perform this action.")
    public ResponseEntity<CityResponse> updateCity(
            @PathVariable Long id,
            @RequestBody @Valid CityRequest cityRequest) {
        CityResponse updatedCity = cityService.updateCity(id, cityRequest);
        return ResponseEntity.ok(updatedCity);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a city", description = "Deletes a city by its ID. Only admins can perform this action.")
    public ResponseEntity<String> deleteCity(@PathVariable Long id) {
        cityService.deleteCity(id);
        return ResponseEntity.ok("City deleted successfully");
    }

    @GetMapping("/search")
    @Operation(summary = "Search cities", description = "Searches for cities by name and/or country.")
    public ResponseEntity<List<CityResponse>> searchCities(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String country) {
        List<CityResponse> cities = cityService.searchCities(name, country);
        return ResponseEntity.ok(cities);
    }
}