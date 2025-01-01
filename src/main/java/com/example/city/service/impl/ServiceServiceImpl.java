package com.example.city.service.impl;

import com.example.city.model.dto.request.ServiceRequest;
import com.example.city.model.dto.response.DetailServiceResponse;
import com.example.city.model.dto.response.ServiceResponse;
import com.example.city.model.entity.City;
import com.example.city.model.entity.Review;
import com.example.city.model.entity.Service;
import com.example.city.model.entity.ServiceCategory;
import com.example.city.model.entity.Visit;
import com.example.city.repository.*;
import com.example.city.service.ServiceService;

import jakarta.persistence.EntityNotFoundException;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.*;
import java.io.IOException;
import java.util.UUID;
import org.springframework.util.StringUtils;
import com.example.city.model.entity.ServiceImage;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.Map;
import java.util.Collections;

@org.springframework.stereotype.Service
public class ServiceServiceImpl implements ServiceService {
        private static final Logger logger = LoggerFactory.getLogger(ServiceServiceImpl.class);
        private final ServiceRepository serviceRepository;
        private final CityRepository cityRepository;
        private final ServiceCategoryRepository serviceCategoryRepository;
        private final VisitRepository visitRepository;
        private final ReviewRepository reviewRepository;
        private final ModelMapper modelMapper;
        private final ServiceImageRepository serviceImageRepository;

        // Define the upload directory
        private static final String UPLOAD_DIR = "uploads/";

        public ServiceServiceImpl(ServiceRepository serviceRepository,
                        CityRepository cityRepository,
                        ServiceCategoryRepository serviceCategoryRepository,
                        VisitRepository visitRepository,
                        ReviewRepository reviewRepository,
                        ModelMapper modelMapper,
                        ServiceImageRepository serviceImageRepository) {
                this.serviceRepository = serviceRepository;
                this.cityRepository = cityRepository;
                this.serviceCategoryRepository = serviceCategoryRepository;
                this.visitRepository = visitRepository;
                this.modelMapper = modelMapper;
                this.reviewRepository = reviewRepository;
                this.serviceImageRepository = serviceImageRepository;

                modelMapper.typeMap(ServiceRequest.class, Service.class).addMappings(mapper -> {
                        mapper.skip(Service::setId);
                });
        }

        @Override
        @Transactional
        public ServiceResponse createService(Long cityId, ServiceRequest serviceRequest, MultipartFile[] images) {
                City city = cityRepository.findById(cityId)
                                .orElseThrow(() -> new RuntimeException("City not found with ID: " + cityId));

                ServiceCategory serviceCategory = serviceCategoryRepository.findById(serviceRequest.getCategoryId())
                                .orElseThrow(() -> new RuntimeException("Service category not found with ID: "
                                                + serviceRequest.getCategoryId()));

                Service service = modelMapper.map(serviceRequest, Service.class);
                service.setCity(city);
                service.setCategory(serviceCategory);

                service.setId(null);

                // Save the service
                Service savedService = serviceRepository.save(service);

                // Handle image uploads
                if (images != null && images.length > 0) {
                        for (MultipartFile image : images) {
                                if (!image.isEmpty()) {
                                        try {
                                                // Ensure the upload directory exists
                                                Path uploadPath = Paths.get(UPLOAD_DIR);
                                                if (!Files.exists(uploadPath)) {
                                                        Files.createDirectories(uploadPath);
                                                }

                                                // Generate a unique filename
                                                String filename = UUID.randomUUID().toString() + "_" + StringUtils.cleanPath(image.getOriginalFilename());

                                                // Save the file to the upload directory
                                                Path filePath = uploadPath.resolve(filename);
                                                Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                                                // Create and save the ServiceImage entity
                                                ServiceImage serviceImage = ServiceImage.builder()
                                                        .imageUrl(filename) // Store only the filename
                                                        .service(savedService)
                                                        .build();

                                                serviceImageRepository.save(serviceImage);
                                        } catch (IOException e) {
                                                throw new RuntimeException("Failed to store image " + image.getOriginalFilename(), e);
                                        }
                                }
                        }
                }

                // Map to ServiceResponse and set additional fields
                ServiceResponse serviceResponse = modelMapper.map(savedService, ServiceResponse.class);
                serviceResponse.setCategoryName(savedService.getCategory().getName());
                serviceResponse.setCityName(savedService.getCity().getName());
                serviceResponse.setImageUrls(
                        serviceImageRepository.findByServiceId(savedService.getId())
                                .stream()
                                .map(ServiceImage::getImageUrl)
                                .collect(Collectors.toList())
                );

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

                // Extract service IDs for bulk image retrieval
                List<Long> serviceIds = services.stream()
                        .map(Service::getId)
                        .collect(Collectors.toList());

                // Fetch all images for the services in one query
                List<ServiceImage> serviceImages = serviceImageRepository.findByServiceIdIn(serviceIds);

                // Group image URLs by service ID
                Map<Long, List<String>> serviceImageMap = serviceImages.stream()
                        .collect(Collectors.groupingBy(
                                image -> image.getService().getId(),
                                Collectors.mapping(ServiceImage::getImageUrl, Collectors.toList())
                        ));

                // Map services to ServiceResponse DTOs with imageUrls
                return services.stream()
                        .map(service -> {
                                ServiceResponse response = modelMapper.map(service, ServiceResponse.class);
                                response.setCategoryName(service.getCategory().getName());
                                response.setCityName(city.getName());

                                // Set image URLs for the service
                                List<String> imageUrls = serviceImageMap.getOrDefault(service.getId(), Collections.emptyList());
                                response.setImageUrls(imageUrls);

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

                // Fetch and set image URLs
                List<String> imageUrls = serviceImageRepository.findByServiceId(id)
                        .stream()
                        .map(ServiceImage::getImageUrl)
                        .collect(Collectors.toList());
                res.setImageUrls(imageUrls);

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
        @Transactional
        public ServiceResponse updateService(Long cityId, Long serviceId, ServiceRequest serviceRequest, MultipartFile[] images, List<String> imagesToDelete) {
                City city = cityRepository.findById(cityId)
                        .orElseThrow(() -> new RuntimeException("City not found with ID: " + cityId));

                ServiceCategory serviceCategory = serviceCategoryRepository.findById(serviceRequest.getCategoryId())
                        .orElseThrow(() -> new RuntimeException("Service category not found with ID: " + serviceRequest.getCategoryId()));

                Service existingService = serviceRepository.findById(serviceId)
                        .orElseThrow(() -> new RuntimeException("Service not found with ID: " + serviceId));

                existingService.setName(serviceRequest.getName());
                existingService.setDescription(serviceRequest.getDescription());
                existingService.setAddress(serviceRequest.getAddress());
                existingService.setContactInfo(serviceRequest.getContactInfo());
                existingService.setOperatingHours(serviceRequest.getOperatingHours());
                existingService.setCategory(serviceCategory);

                // Handle image deletions
                if (imagesToDelete != null && !imagesToDelete.isEmpty()) {
                        for (String filename : imagesToDelete) {
                                // Delete file from filesystem
                                Path filePath = Paths.get(UPLOAD_DIR).resolve(filename).normalize();
                                try {
                                        Files.deleteIfExists(filePath);
                                } catch (IOException e) {
                                        logger.error("Failed to delete image file: {}", filename, e);
                                        throw new RuntimeException("Failed to delete image " + filename, e);
                                }

                                // Delete from database
                                ServiceImage serviceImage = serviceImageRepository.findByServiceIdAndImageUrl(serviceId, filename)
                                        .orElseThrow(() -> new RuntimeException("Image not found: " + filename));
                                serviceImageRepository.delete(serviceImage);
                        }
                }

                // Handle new image uploads
                if (images != null && images.length > 0) {
                        for (MultipartFile image : images) {
                                if (!image.isEmpty()) {
                                        try {
                                                // Ensure the upload directory exists
                                                Path uploadPath = Paths.get(UPLOAD_DIR);
                                                if (!Files.exists(uploadPath)) {
                                                        Files.createDirectories(uploadPath);
                                                }

                                                // Generate a unique filename
                                                String filename = UUID.randomUUID().toString() + "_" + StringUtils.cleanPath(image.getOriginalFilename());

                                                // Save the file to the upload directory
                                                Path filePath = uploadPath.resolve(filename);
                                                Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                                                // Create and save the ServiceImage entity
                                                ServiceImage serviceImage = ServiceImage.builder()
                                                        .imageUrl(filename) // Store only the filename
                                                        .service(existingService)
                                                        .build();

                                                serviceImageRepository.save(serviceImage);
                                        } catch (IOException e) {
                                                throw new RuntimeException("Failed to store image " + image.getOriginalFilename(), e);
                                        }
                                }
                        }
                }

                // Save the updated service
                Service updatedService = serviceRepository.save(existingService);

                // Map to ServiceResponse and set additional fields
                ServiceResponse serviceResponse = modelMapper.map(updatedService, ServiceResponse.class);
                serviceResponse.setCategoryName(updatedService.getCategory().getName());
                serviceResponse.setCityName(updatedService.getCity().getName());
                serviceResponse.setImageUrls(
                        serviceImageRepository.findByServiceId(updatedService.getId())
                                .stream()
                                .map(ServiceImage::getImageUrl)
                                .collect(Collectors.toList())
                );

                return serviceResponse;
        }

        @Override
        @Transactional
        public void deleteService(Long cityId, Long serviceId) {
                Service service = serviceRepository.findById(serviceId)
                        .orElseThrow(() -> new RuntimeException("Service not found with ID: " + serviceId));

                // Retrieve associated images
                List<ServiceImage> images = serviceImageRepository.findByServiceId(serviceId);
                for (ServiceImage image : images) {
                        // Delete file from filesystem
                        Path filePath = Paths.get(UPLOAD_DIR).resolve(image.getImageUrl()).normalize();
                        try {
                                Files.deleteIfExists(filePath);
                        } catch (IOException e) {
                                logger.error("Failed to delete image file: {}", image.getImageUrl(), e);
                                throw new RuntimeException("Failed to delete image " + image.getImageUrl(), e);
                        }

                        // Delete from database
                        serviceImageRepository.delete(image);
                }

                // Delete the service
                serviceRepository.delete(service);
        }

        @Override
        @Transactional(readOnly = true)
        public List<ServiceResponse> getAllServices() {
                List<Service> services = serviceRepository.findAll();

                List<Long> serviceIds = services.stream()
                        .map(Service::getId)
                        .collect(Collectors.toList());

                List<ServiceImage> serviceImages = serviceImageRepository.findByServiceIdIn(serviceIds);

                Map<Long, List<String>> serviceImageMap = serviceImages.stream()
                        .collect(Collectors.groupingBy(
                                image -> image.getService().getId(),
                                Collectors.mapping(ServiceImage::getImageUrl, Collectors.toList())
                        ));

                return services.stream()
                        .map(service -> {
                                ServiceResponse response = modelMapper.map(service, ServiceResponse.class);
                                response.setCategoryName(service.getCategory().getName());
                                response.setCityName(service.getCity().getName());
                                response.setImageUrls(serviceImageMap.getOrDefault(service.getId(), Collections.emptyList()));
                                return response;
                        })
                        .collect(Collectors.toList());
        }
}