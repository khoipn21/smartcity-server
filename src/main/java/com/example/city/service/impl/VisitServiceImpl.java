package com.example.city.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.city.model.dto.response.ServiceResponse;
import com.example.city.model.dto.response.VisitResponse;
import com.example.city.model.dto.response.VisitServiceResponse;
import com.example.city.model.entity.Review;
import com.example.city.model.entity.Service;
import com.example.city.model.entity.User;
import com.example.city.model.entity.Visit;
import com.example.city.repository.ReviewRepository;
import com.example.city.repository.ServiceRepository;
import com.example.city.repository.UserRepository;
import com.example.city.repository.VisitRepository;
import com.example.city.service.VisitService;

@org.springframework.stereotype.Service
public class VisitServiceImpl implements VisitService{

   private final VisitRepository visitRepository;

   private final ServiceRepository serviceRepository;

   private final UserRepository userRepository;

   private final ReviewRepository reviewRepository;

   public VisitServiceImpl(
      VisitRepository visitRepository,
      ServiceRepository serviceRepository,
      UserRepository userRepository,
      ReviewRepository reviewRepository
   ) {
      this.visitRepository = visitRepository;
      this.serviceRepository = serviceRepository;
      this.userRepository = userRepository;
      this.reviewRepository = reviewRepository;
   }

   @Override
   public void addVisitToService(Long ServiceId) {
      Optional<Service> service = this.serviceRepository.findById(ServiceId);

      if(service == null) {
         throw new RuntimeException("Service not exist with id" + ServiceId);
      }

      Authentication authentication  = SecurityContextHolder.getContext().getAuthentication();
      UserDetails userDetails = (UserDetails) authentication.getPrincipal();
      String userName = userDetails.getUsername();
      User user = this.userRepository.findByUsername(userName).get();
      
      boolean checkVisit = this.visitRepository.existsByUserIdAndServiceId(user.getId(), service.get().getId());
      if(checkVisit) {
         throw new RuntimeException("This service has been visited");
      }

      Visit visit = Visit.builder().user(user).service(service.get()).build();

      visitRepository.save(visit);
   } 

   public List<VisitResponse> getAllVisits() {
      List<Visit> visits = visitRepository.findAll();
      return visits.stream().map(visit -> VisitResponse.builder()
              .id(visit.getId())
              .userId(visit.getUser().getId())
              .userName(visit.getUser().getUsername()) 
              .serviceId(visit.getService().getId())
              .serviceName(visit.getService().getName()) 
              .visitDate(visit.getVisitDate())
              .build()
      ).collect(Collectors.toList());
  }

  public VisitServiceResponse getAllVisitService(Long serviceId) {
      List<Visit> visits = this.visitRepository.findByServiceId(serviceId);
      VisitServiceResponse res = new VisitServiceResponse();
     
      float sum = 0f;
      int count = 0;
      List<VisitResponse> visitResponses = new ArrayList<>();

      for (Visit visit : visits) {
         Review review = this.reviewRepository.findByVisitId(visit.getId());
         if(review != null) {
            count ++;
            sum += review.getRating();
         }

         visitResponses.add(
            VisitResponse.builder()
               .id(visit.getId())
               .userId(visit.getUser().getId())
               .userName(visit.getUser().getUsername())
               .serviceId(visit.getService().getId())
               .serviceName(visit.getService().getName())
               .visitDate(visit.getVisitDate())
               .build());
      }

      float averageRating = sum / count;
      res.setAverageRating(averageRating);
      res.setListVisit(visitResponses);
      return res;
   }

}