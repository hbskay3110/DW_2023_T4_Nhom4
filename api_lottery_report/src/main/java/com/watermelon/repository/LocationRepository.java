package com.watermelon.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.watermelon.model.entity.Location;


public interface LocationRepository extends JpaRepository<Location, Long>{

	List<Location> findByRegion(String region);
}
