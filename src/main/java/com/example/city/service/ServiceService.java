package com.example.city.service;

import com.example.city.model.dto.request.ServiceRequest;
import com.example.city.model.dto.response.DetailServiceResponse;
import com.example.city.model.dto.response.ServiceResponse;

import java.util.List;

public interface ServiceService {
    ServiceResponse createService(Long cityId, ServiceRequest serviceRequest);

    List<ServiceResponse> getAllServicesInCity(Long cityId);

    DetailServiceResponse getServiceById(Long id);

    ServiceResponse updateService(Long id, ServiceRequest serviceRequest);

    void deleteService(Long id);
}