package com.example.city.repository;

import com.example.city.model.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    boolean existsByName(String name);

    List<Service> findByCityId(Long cityId);
}