package com.example.city.service.impl;

import com.example.city.model.dto.request.CityRequest;
import com.example.city.model.dto.response.CityResponse;
import com.example.city.model.entity.City;
import com.example.city.repository.CityRepository;
import com.example.city.service.CityService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CityServiceImpl implements CityService {
    private final CityRepository cityRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public CityServiceImpl(CityRepository cityRepository, ModelMapper modelMapper) {
        this.cityRepository = cityRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<CityResponse> getAllCities() {
        return cityRepository.findAll().stream().map(city -> modelMapper.map(city, CityResponse.class)).collect(Collectors.toList());
    }

    @Override
    public CityResponse getCityById(Long id) {
        City city = cityRepository.findById(id).orElseThrow(() -> new RuntimeException("City not found" + id));
        return modelMapper.map(city, CityResponse.class);
    }

    @Override
    public CityResponse addCity(CityRequest cityRequest) {
//        if (cityRepository.existsByNameAndCountry(cityRequest.getName(), cityRequest.getCountry())) {
//            throw new RuntimeException("City with the same name and country already exists");
//        }
        City city = City.builder()
                .name(cityRequest.getName())
                .country(cityRequest.getCountry())
                .description(cityRequest.getDescription())
                .build();
        City savedCity = cityRepository.save(city);
        return modelMapper.map(savedCity, CityResponse.class);
    }

}
