package com.example.city.service;

import com.example.city.model.dto.request.ServiceRequest;
import com.example.city.model.dto.response.DetailServiceResponse;
import com.example.city.model.dto.response.ServiceResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ServiceService {
    ServiceResponse createService(Long cityId, ServiceRequest serviceRequest, MultipartFile[] images);

    List<ServiceResponse> getAllServicesInCity(Long cityId);

    DetailServiceResponse getServiceById(Long id);

    ServiceResponse updateService(Long cityId, Long serviceId, ServiceRequest serviceRequest, MultipartFile[] images, List<String> imagesToDelete);

    void deleteService(Long cityId, Long serviceId);

    List<ServiceResponse> getAllServices();
}