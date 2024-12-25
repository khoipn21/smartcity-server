package com.example.city.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.city.model.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
   Review findByVisitId(Long Id);

   boolean existsByVisitId(Long Id);
}
