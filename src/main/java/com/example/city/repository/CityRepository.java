package com.example.city.repository;

import com.example.city.model.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
    boolean existsByNameAndCountry(String name, String country);

    boolean existsByNameAndCountryAndIdNot(String name, String country, Long id);
}