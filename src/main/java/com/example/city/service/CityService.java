package com.example.city.service;

import com.example.city.model.dto.request.CityRequest;
import com.example.city.model.dto.response.CityResponse;

import java.util.List;

public interface CityService {
    List<CityResponse> getAllCities();

    CityResponse getCityById(Long id);

    CityResponse addCity(CityRequest cityRequest);

    CityResponse updateCity(Long id, CityRequest cityRequest);

    void deleteCity(Long id);

    List<CityResponse> searchCities(String name, String country);
}
