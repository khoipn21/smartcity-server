package com.example.city.service.impl;

import com.example.city.model.dto.request.ServiceRequest;
import com.example.city.model.dto.response.DetailServiceResponse;
import com.example.city.model.dto.response.ServiceResponse;
import com.example.city.model.entity.City;
import com.example.city.model.entity.Review;
import com.example.city.model.entity.Service;
import com.example.city.model.entity.ServiceCategory;
import com.example.city.model.entity.Visit;
import com.example.city.repository.CityRepository;
import com.example.city.repository.ReviewRepository;
import com.example.city.repository.ServiceCategoryRepository;
import com.example.city.repository.ServiceRepository;
import com.example.city.repository.VisitRepository;
import com.example.city.service.ServiceService;

import jakarta.persistence.EntityNotFoundException;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class ServiceServiceImpl implements ServiceService {
        private static final Logger logger = LoggerFactory.getLogger(ServiceServiceImpl.class);
        private final ServiceRepository serviceRepository;
        private final CityRepository cityRepository;
        private final ServiceCategoryRepository serviceCategoryRepository;
        private final VisitRepository visitRepository;
        private final ReviewRepository reviewRepository;
        private final ModelMapper modelMapper;

        public ServiceServiceImpl(ServiceRepository serviceRepository,
                        CityRepository cityRepository,
                        ServiceCategoryRepository serviceCategoryRepository,
                        VisitRepository visitRepository,
                        ReviewRepository reviewRepository,
                        ModelMapper modelMapper) {
                this.serviceRepository = serviceRepository;
                this.cityRepository = cityRepository;
                this.serviceCategoryRepository = serviceCategoryRepository;
                this.visitRepository = visitRepository;
                this.modelMapper = modelMapper;
                this.reviewRepository = reviewRepository;
        }

        @Override
        @Transactional
        public ServiceResponse createService(Long cityId, ServiceRequest serviceRequest) {
                // System.out.println("Creating service with name: {}" +
                // serviceRequest.getName());

                City city = cityRepository.findById(cityId)
                                .orElseThrow(() -> new RuntimeException("City not found with ID: " + cityId));
                // System.out.println("Found city: {}" + city.getName());

                ServiceCategory serviceCategory = serviceCategoryRepository.findById(serviceRequest.getCategoryId())
                                .orElseThrow(() -> new RuntimeException("Service category not found with ID: "
                                                + serviceRequest.getCategoryId()));
                // System.out.println("Found service category: {}" + serviceCategory.getName());

                // if (serviceRepository.existsByName(serviceRequest.getName())) {
                // logger.warn("Service with name '{}' already exists",
                // serviceRequest.getName());
                // throw new RuntimeException("Service with name " + serviceRequest.getName() +
                // " already exists");
                // }

                Service service = modelMapper.map(serviceRequest, Service.class);
                service.setCity(city);
                service.setCategory(serviceCategory);
                // System.out.println("Mapped service entity: {}" + service);

                Service savedService = serviceRepository.save(service);
                // System.out.println("Saved service with ID: {}" + savedService.getId());

                ServiceResponse serviceResponse = modelMapper.map(savedService, ServiceResponse.class);
                serviceResponse.setCategoryName(serviceCategory.getName());
                serviceResponse.setCityName(city.getName());
                // System.out.println("Mapped service response: {}" + serviceResponse);

                return serviceResponse;
        }

        @Override
        @Transactional(readOnly = true)
        public List<ServiceResponse> getAllServicesInCity(Long cityId) {
                // Validate city existence
                City city = cityRepository.findById(cityId)
                                .orElseThrow(() -> new RuntimeException("City not found with ID: " + cityId));

                // Retrieve all services for the city
                List<Service> services = serviceRepository.findByCityId(cityId);

                // Map entities to response DTOs
                return services.stream()
                                .map(service -> {
                                        ServiceResponse response = modelMapper.map(service, ServiceResponse.class);
                                        response.setCategoryName(service.getCategory().getName());
                                        response.setCityName(city.getName());
                                        return response;
                                })
                                .collect(Collectors.toList());
        }

        @Override
        public DetailServiceResponse getServiceById(Long id) {
                DetailServiceResponse res = new DetailServiceResponse();

                Service currentService = serviceRepository.findById(id).get();
                
                if(currentService == null) {
                        throw new RuntimeException("Service not found with ID: " + id);
                }

                res.setId(currentService.getId());
                res.setAddress(currentService.getAddress());
                res.setName(currentService.getName());
                res.setContactInfo(currentService.getContactInfo());
                res.setDescription(currentService.getDescription());
                res.setCreatedAt(currentService.getCreatedAt());
                res.setOperatingHours(currentService.getOperatingHours());

                List<Visit> visits = this.visitRepository.findByServiceId(currentService.getId());

                if (!visits.isEmpty()) {
                        List<DetailServiceResponse.ReviewService> rs = visits.stream().map(visit -> {
                                Review review = this.reviewRepository.findByVisitId(visit.getId());
                                if (review != null) {
                                        DetailServiceResponse.ReviewService tempReview = new DetailServiceResponse.ReviewService();
                                        tempReview.setId(review.getId());
                                        tempReview.setComment(review.getComment());
                                        tempReview.setRating(review.getRating());
                                        return tempReview;
                                }
                                return null;
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                        res.setReviewService(rs);
                }

                return res;
        }

        @Override
        public ServiceResponse updateService(Long id, ServiceRequest serviceRequest) {
                boolean exists = serviceRepository.existsByNameAndIdNot(serviceRequest.getName(), id);
                if (exists) {
                        throw new RuntimeException("Service with the same name already exists");
                }
                Service existingService = serviceRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Service not found with ID: " + id));

                existingService.setName(serviceRequest.getName());
                existingService.setDescription(serviceRequest.getDescription());
                existingService.setAddress(serviceRequest.getAddress());

                Service updatedService = serviceRepository.save(existingService);
                return modelMapper.map(updatedService, ServiceResponse.class);
        }

        @Override
        public void deleteService(Long id) {
                Service service = serviceRepository.findById(id).orElseThrow(() -> new RuntimeException("Service not found" + id));
                serviceRepository.delete(service);
        }
}