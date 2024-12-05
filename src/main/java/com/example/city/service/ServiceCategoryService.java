package com.example.city.service;

import com.example.city.model.dto.request.ServiceCategoryRequest;
import com.example.city.model.dto.response.ServiceCategoryResponse;

public interface ServiceCategoryService {
    ServiceCategoryResponse createServiceCategory(ServiceCategoryRequest serviceCategoryRequest);
}
