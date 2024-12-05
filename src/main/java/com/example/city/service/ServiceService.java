package com.example.city.service;

import com.example.city.model.dto.request.ServiceRequest;
import com.example.city.model.dto.response.ServiceResponse;

import java.util.List;

public interface ServiceService {
    ServiceResponse createService(Long cityId, ServiceRequest serviceRequest);

    List<ServiceResponse> getAllServicesInCity(Long cityId);
}