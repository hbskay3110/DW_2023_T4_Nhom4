package com.watermelon.model.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class LocationResponseDTO {

	private long id;
	private String region;
	private List<String> listLocations;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public List<String> getListLocations() {
		return listLocations;
	}
	public void setListLocations(List<String> listLocations) {
		this.listLocations = listLocations;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
	
	
}
