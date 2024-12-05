package com.example.city.repository;

import com.example.city.model.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
    boolean existsByNameAndCountry(String name, String country);

    boolean existsByNameAndCountryAndIdNot(String name, String country, Long id);

    //    boolean existsByCity(Long id);
    @Query("SELECT c FROM City c WHERE " +
            "(:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:country IS NULL OR c.country = :country)")
    List<City> searchCities(@Param("name") String name, @Param("country") String country);

}