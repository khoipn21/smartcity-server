package com.example.city.service.impl;

import java.lang.StackWalker.Option;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.city.model.dto.request.ReviewRequest;
import com.example.city.model.dto.response.ReviewResponse;
import com.example.city.model.entity.Review;
import com.example.city.model.entity.Service;
import com.example.city.model.entity.User;
import com.example.city.model.entity.Visit;
import com.example.city.repository.ReviewRepository;
import com.example.city.repository.ServiceRepository;
import com.example.city.repository.UserRepository;
import com.example.city.repository.VisitRepository;
import com.example.city.service.ReviewService;

@org.springframework.stereotype.Service
public class ReviewServiceImpl implements ReviewService {

   private final ReviewRepository reviewRepository;

   private final ServiceRepository serviceRepository;

   private final UserRepository userRepository;

   private final VisitRepository visitRepository;

   private ModelMapper modelMapper;

   public ReviewServiceImpl(
      ReviewRepository reviewRepository,
      ServiceRepository serviceRepository, 
      UserRepository userRepository,
      VisitRepository visitRepository,
      ModelMapper modelMapper
      
   ) {
      this.reviewRepository = reviewRepository;
      this.serviceRepository = serviceRepository;
      this.userRepository = userRepository;
      this.visitRepository = visitRepository;
      this.modelMapper = modelMapper;
   }

   @Override
   public ReviewResponse addReviewService(Long ServiceId, ReviewRequest reviewRequest) {
      Optional<Service> service = this.serviceRepository.findById(ServiceId);

      if(service == null) {
         throw new RuntimeException("Service not exist with id " + ServiceId);
      }

      Authentication authentication  = SecurityContextHolder.getContext().getAuthentication();
      UserDetails userDetails = (UserDetails) authentication.getPrincipal();
      String userName = userDetails.getUsername();
      Long UserId = userRepository.findByUsername(userName).get().getId();
      
      Optional<Visit> visit = this.visitRepository.findByUserIdAndServiceId(UserId, ServiceId);

      if(!visit.isPresent()) {
         throw new RuntimeException("Visit not exist with User_Id " + UserId + " and service_id " + ServiceId);
      }
      Review review = modelMapper.map(reviewRequest, Review.class);
      review.setVisit(visit.get());

      boolean existVisitInReview = this.reviewRepository.existsByVisitId(visit.get().getId());

      if(existVisitInReview) {
         throw new RuntimeException("service has been reviewed");
      }

      Review savedReview = this.reviewRepository.save(review);
      ReviewResponse res = modelMapper.map(savedReview, ReviewResponse.class);

      return res;
   }
}
