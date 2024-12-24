package com.example.city.repository;

import com.example.city.model.entity.ServiceImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceImageRepository extends JpaRepository<ServiceImage, Long> {
    List<ServiceImage> findByServiceId(Long serviceId);
} 