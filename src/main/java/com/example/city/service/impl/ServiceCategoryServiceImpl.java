package com.example.city.service.impl;

import com.example.city.model.dto.request.ServiceCategoryRequest;
import com.example.city.model.dto.response.ServiceCategoryResponse;
import com.example.city.model.entity.ServiceCategory;
import com.example.city.repository.ServiceCategoryRepository;
import com.example.city.service.ServiceCategoryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class ServiceCategoryServiceImpl implements ServiceCategoryService {
    private final ServiceCategoryRepository serviceCategoryRepository;
    private final ModelMapper modelMapper;

    public ServiceCategoryServiceImpl(ServiceCategoryRepository serviceCategoryRepository, ModelMapper modelMapper) {
        this.serviceCategoryRepository = serviceCategoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ServiceCategoryResponse createServiceCategory(ServiceCategoryRequest serviceCategoryRequest) {
        if (serviceCategoryRepository.existsByName(serviceCategoryRequest.getName())) {
            throw new RuntimeException("Service category with name " + serviceCategoryRequest.getName() + " already exists");
        }
        ServiceCategory serviceCategory = modelMapper.map(serviceCategoryRequest, ServiceCategory.class);
        serviceCategoryRepository.save(serviceCategory);
        return modelMapper.map(serviceCategory, ServiceCategoryResponse.class);
    }
}
