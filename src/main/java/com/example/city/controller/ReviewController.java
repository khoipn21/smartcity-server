package com.example.city.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.city.model.dto.request.ReviewRequest;
import com.example.city.model.dto.response.ReviewResponse;
import com.example.city.model.entity.Review;
import com.example.city.service.ReviewService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/services/{serviceId}/reviews")
@Tag(name = "Review", description = "APIs for managing review within a city")
public class ReviewController {

   private final ReviewService reviewService;
   
   public ReviewController(ReviewService reviewService) {
      this.reviewService = reviewService;
   }

   @PostMapping
   @PreAuthorize("hasRole('USER')")
   @Operation(summary = "Add a Review to Service", description = "Adds a new review to a specific service. Only user can perform this action.")
   public ResponseEntity<ReviewResponse> addReviewService(
         @PathVariable Long serviceId,
         @RequestBody @Valid ReviewRequest reviewRequest) {

            ReviewResponse reviewResponse = this.reviewService.addReviewService(serviceId, reviewRequest);

      return ResponseEntity.ok(reviewResponse);
   }
}
