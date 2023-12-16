package com.watermelon.service.impl;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.watermelon.model.entity.Location;
import com.watermelon.model.mapper.ObjectMapper;
import com.watermelon.model.response.LocationResponseDTO;
import com.watermelon.model.response.RegionResponseDTO;
import com.watermelon.repository.LocationRepository;
import com.watermelon.service.LocationService;

@Service
public class LocationServiceImpl implements LocationService {

	@Autowired
	private LocationRepository locationRepository;

	private ObjectMapper objectMapper = new ObjectMapper();

	@Transactional
	@Override
	public LocationResponseDTO getLocationById(long id) {
		Location locationEntity = locationRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Location not found with id: " + id));

		LocationResponseDTO responseDTO = objectMapper.mapToLocationResponseDTO(locationEntity);
		responseDTO.setListLocations(getListLocationByRegion(locationEntity.getRegion()));
		return responseDTO;
	}

	private List<String> getListLocationByRegion(String region) {
		// Lấy danh sách location theo region từ CSDL và chuyển đổi thành List<String>
		// Implement logic này tùy thuộc vào cách bạn tổ chức dữ liệu trong CSDL của
		// mình.
		// Ví dụ, nếu có một cột "location" trong bảng, bạn có thể thực hiện một query
		// để lấy danh sách.
		return locationRepository.findByRegion(region).stream()
				.map(Location::getLocation).collect(Collectors.toList());
	}

	@Override
	public List<RegionResponseDTO> getAllRegions() {
		List<Location> allLocations = locationRepository.findAll();

		// Group locations by region
		Map<String, List<Location>> locationsByRegion = allLocations.stream()
				.collect(Collectors.groupingBy(Location::getRegion));

		// Convert to RegionResponseDTO
		return locationsByRegion.entrySet().stream().map(entry -> {
			RegionResponseDTO regionResponseDTO = new RegionResponseDTO();
			regionResponseDTO.setId(entry.getValue().get(0).getId());
			regionResponseDTO.setRegion(entry.getKey());
			regionResponseDTO.setListLocations(
					entry.getValue().stream()
					.sorted(Comparator.comparing(Location::getId))
					.map(Location::getLocation)
					.collect(Collectors.toList()));
			return regionResponseDTO;
		}).collect(Collectors.toList());
	}

}
