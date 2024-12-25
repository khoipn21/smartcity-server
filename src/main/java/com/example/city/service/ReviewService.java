package com.example.city.service;

import com.example.city.model.dto.request.ReviewRequest;
import com.example.city.model.dto.response.ReviewResponse;

public interface ReviewService {

   ReviewResponse addReviewService(Long id, ReviewRequest review);
   
}
