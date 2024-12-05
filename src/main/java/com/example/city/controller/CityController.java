package com.example.city.controller;

import com.example.city.model.dto.request.CityRequest;
import com.example.city.model.dto.request.CityUpdateRequest;
import com.example.city.model.dto.response.CityResponse;
import com.example.city.service.CityService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cities")
public class CityController {
    private final CityService cityService;

    @Autowired
    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    //Get all cities
    @GetMapping
    public ResponseEntity<List<CityResponse>> getAllCities() {
        List<CityResponse> cities = cityService.getAllCities();
        return ResponseEntity.ok(cities);
    }

    //Get city by id
    @GetMapping("/{id}")
    public ResponseEntity<CityResponse> getCityById(@PathVariable Long id) {
        CityResponse city = cityService.getCityById(id);
        return ResponseEntity.ok(city);
    }

    //Add city
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CityResponse> addCity(@RequestBody @Valid CityRequest cityRequest) {
        CityResponse createdCity = cityService.addCity(cityRequest);
        return ResponseEntity.ok(createdCity);
    }

    //Update city
    @PutMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CityResponse> updateCity(
            @PathVariable Long id,
            @RequestBody @Valid CityUpdateRequest cityUpdateRequest) {
        CityResponse updatedCity = cityService.updateCity(id, cityUpdateRequest);
        return ResponseEntity.ok(updatedCity);
    }

    //Delete city
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteCity(@PathVariable Long id) {
        cityService.deleteCity(id);
        return ResponseEntity.ok("City deleted successfully");
    }

    //Search cities
    @GetMapping("/search")
    public ResponseEntity<List<CityResponse>> searchCities(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String country) {
        List<CityResponse> cities = cityService.searchCities(name, country);
        return ResponseEntity.ok(cities);
    }
}