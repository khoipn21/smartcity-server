package com.example.city.service;

import com.example.city.model.dto.request.ServiceCategoryRequest;
import com.example.city.model.dto.response.ServiceCategoryResponse;

import java.util.List;

public interface ServiceCategoryService {
    ServiceCategoryResponse createServiceCategory(ServiceCategoryRequest serviceCategoryRequest);

    List<ServiceCategoryResponse> getAllServiceCategories();

    ServiceCategoryResponse getServiceCategoryById(Long categoryId);

    ServiceCategoryResponse updateServiceCategory(Long categoryId, ServiceCategoryRequest serviceCategoryUpdateRequest);

}
