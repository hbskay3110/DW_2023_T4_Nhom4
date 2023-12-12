package com.watermelon.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.watermelon.model.response.LocationResponseDTO;
import com.watermelon.model.response.RegionResponseDTO;

public interface LocationService {

	List<RegionResponseDTO> getAllRegions();
	LocationResponseDTO getLocationById(long id);
}
