package com.watermelon.model.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.watermelon.model.entity.Location;
import com.watermelon.model.response.LocationResponseDTO;

public class ObjectMapper {

	public LocationResponseDTO mapToLocationResponseDTO(Location locationEntity) {
        LocationResponseDTO responseDTO = new LocationResponseDTO();
        responseDTO.setId(locationEntity.getId());
        responseDTO.setRegion(locationEntity.getRegion());
        responseDTO.setCreatedAt(locationEntity.getCreatedAt());
        responseDTO.setUpdatedAt(locationEntity.getUpdatedAt());

        return responseDTO;
    }
	
	
	
}
