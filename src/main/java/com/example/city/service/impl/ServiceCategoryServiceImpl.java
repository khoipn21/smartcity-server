package com.example.city.service.impl;

import com.example.city.model.dto.request.ServiceCategoryRequest;
import com.example.city.model.dto.response.ServiceCategoryResponse;
import com.example.city.model.entity.ServiceCategory;
import com.example.city.repository.ServiceCategoryRepository;
import com.example.city.service.ServiceCategoryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public List<ServiceCategoryResponse> getAllServiceCategories() {
        List<ServiceCategory> categories = serviceCategoryRepository.findAll();
        return categories.stream()
                .map(category -> modelMapper.map(category, ServiceCategoryResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public ServiceCategoryResponse updateServiceCategory(Long categoryId,
                                                         ServiceCategoryRequest serviceCategoryRequest) {
        // Fetch the existing service category
        ServiceCategory existingCategory = serviceCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Service category not found with ID: " + categoryId));

        // Check if the updated name already exists (and is not the current category)
        if (serviceCategoryRequest.getName() != null &&
                !serviceCategoryRequest.getName().equals(existingCategory.getName()) &&
                serviceCategoryRepository.existsByName(serviceCategoryRequest.getName())) {
            throw new RuntimeException("Service category with name " + serviceCategoryRequest.getName() + " already " +
                    "exists");
        }

        // Update fields if they are provided
        if (serviceCategoryRequest.getName() != null) {
            existingCategory.setName(serviceCategoryRequest.getName());
        }
        if (serviceCategoryRequest.getDescription() != null) {
            existingCategory.setDescription(serviceCategoryRequest.getDescription());
        }

        // Save the updated entity
        ServiceCategory updatedCategory = serviceCategoryRepository.save(existingCategory);

        // Map to response DTO
        return modelMapper.map(updatedCategory, ServiceCategoryResponse.class);
    }

    @Override
    public ServiceCategoryResponse getServiceCategoryById(Long categoryId) {
        ServiceCategory category =
                serviceCategoryRepository.findById(categoryId).orElseThrow(() -> new RuntimeException("City not found" + categoryId));
        return modelMapper.map(category, ServiceCategoryResponse.class);
    }
}
