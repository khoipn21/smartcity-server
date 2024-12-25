package com.example.city.service;

import java.util.List;

import com.example.city.model.dto.request.ReviewRequest;
import com.example.city.model.dto.response.ReviewResponse;
import com.example.city.model.dto.response.ReviewServiceResponse;

public interface ReviewService {

   ReviewResponse addReviewService(Long id, ReviewRequest review);

   List<ReviewServiceResponse> getReviewsOfService(Long serviceId);
}
