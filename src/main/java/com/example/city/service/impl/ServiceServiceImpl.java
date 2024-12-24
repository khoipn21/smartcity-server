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
import java.util.stream.Collectors;
import java.util.Arrays;

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
                                                        .imageUrl("/" + UPLOAD_DIR + filename)
                                                        .service(savedService)
                                                        .build();

                                                serviceImageRepository.save(serviceImage);
                                        } catch (IOException e) {
                                                throw new RuntimeException("Failed to store image " + image.getOriginalFilename(), e);
                                        }
                                }
                        }
                }

                // Retrieve all image URLs for the service
                List<String> imageUrls = serviceImageRepository.findByServiceId(savedService.getId())
                        .stream()
                        .map(ServiceImage::getImageUrl)
                        .collect(Collectors.toList());

                // Map to ServiceResponse and set additional fields
                ServiceResponse serviceResponse = modelMapper.map(savedService, ServiceResponse.class);
                serviceResponse.setCategoryName(serviceCategory.getName());
                serviceResponse.setCityName(city.getName());
                serviceResponse.setImageUrls(imageUrls);

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
                DetailServiceResponse.ReviewService reviewService = new DetailServiceResponse.ReviewService();

                Service currentService = serviceRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Service not found with ID: " + id));

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
                                        DetailServiceResponse.ReviewService singleReview = new DetailServiceResponse.ReviewService();
                                        singleReview.setId(review.getId());
                                        singleReview.setComment(review.getComment());
                                        singleReview.setRating(review.getRating());
                                        return singleReview;
                                }
                                return null;
                        }).filter(review -> review != null)
                          .collect(Collectors.toList());
                        res.setReviewService(rs);
                }

                return res;
        }

        @Override
        @Transactional
        public ServiceResponse updateService(Long id, ServiceRequest serviceRequest, MultipartFile[] images) {
                boolean exists = serviceRepository.existsByNameAndIdNot(serviceRequest.getName(), id);
                if (exists) {
                        throw new RuntimeException("Service with the same name already exists");
                }

                Service existingService = serviceRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Service not found with ID: " + id));

                existingService.setName(serviceRequest.getName());
                existingService.setDescription(serviceRequest.getDescription());
                existingService.setAddress(serviceRequest.getAddress());

                // Handle image updates
                if (images != null && images.length > 0) {
                        List<ServiceImage> existingImages = serviceImageRepository.findByServiceId(id);
                        for (ServiceImage image : existingImages) {
                                String imagePath = image.getImageUrl().replace("/" + UPLOAD_DIR, UPLOAD_DIR);
                                Path filePath = Paths.get(imagePath);
                                try {
                                        Files.deleteIfExists(filePath);
                                        serviceImageRepository.delete(image);
                                } catch (IOException e) {
                                        throw new RuntimeException("Failed to delete image " + image.getImageUrl(), e);
                                }
                        }

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
                                                        .imageUrl("/" + UPLOAD_DIR + filename)
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

                // Retrieve all image URLs for the service after update
                List<String> imageUrls = serviceImageRepository.findByServiceId(updatedService.getId())
                        .stream()
                        .map(ServiceImage::getImageUrl)
                        .collect(Collectors.toList());

                // Map to ServiceResponse and set additional fields
                ServiceResponse serviceResponse = modelMapper.map(updatedService, ServiceResponse.class);
                serviceResponse.setCategoryName(existingService.getCategory().getName());
                serviceResponse.setCityName(existingService.getCity().getName());
                serviceResponse.setImageUrls(imageUrls);

                return serviceResponse;
        }

        @Override
        public void deleteService(Long id) {
                Service service = serviceRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Service not found with ID: " + id));

                // Retrieve associated images
                List<ServiceImage> images = serviceImageRepository.findByServiceId(id);
                for (ServiceImage image : images) {
                        Path filePath = Paths.get(image.getImageUrl().replace("/" + UPLOAD_DIR, UPLOAD_DIR));
                        try {
                                Files.deleteIfExists(filePath);
                        } catch (IOException e) {
                                logger.error("Failed to delete image file: " + filePath, e);
                        }
                }

                // Delete image records from the database
                serviceImageRepository.deleteAll(images);

                // Delete the service
                serviceRepository.delete(service);
        }
}