package com.example.city.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.city.model.entity.Visit;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long>{
   List<Visit> findByServiceId(Long serviceId);

   Optional<Visit> findByUserIdAndServiceId(Long userId, Long serviceId);

   // boolean existsByUserIdAndServiceId(Long userId, Long serviceId);
}
