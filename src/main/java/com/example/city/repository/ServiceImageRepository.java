package com.example.city.repository;

import com.example.city.model.entity.ServiceImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceImageRepository extends JpaRepository<ServiceImage, Long> {
    List<ServiceImage> findByServiceId(Long serviceId);

    List<ServiceImage> findByServiceIdIn(List<Long> serviceIds);

    Optional<ServiceImage> findByServiceIdAndImageUrl(Long serviceId, String imageUrl);
}