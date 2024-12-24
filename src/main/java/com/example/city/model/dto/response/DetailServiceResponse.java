package com.example.city.model.dto.response;

import java.time.Instant;
import java.util.List;

import com.example.city.model.entity.City;
import com.example.city.model.entity.ServiceCategory;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetailServiceResponse {

   private Long id;

   private String name;

   private String description;

   private String address;

   private String contactInfo;

   private String operatingHours;

   private Instant createdAt;

   private List<ReviewService> ReviewService;

   private List<String> imageUrls;

   @Getter
   @Setter
   public static class ReviewService {
      private Long id;
      private Byte rating;
      private String comment;
   }
   
}
