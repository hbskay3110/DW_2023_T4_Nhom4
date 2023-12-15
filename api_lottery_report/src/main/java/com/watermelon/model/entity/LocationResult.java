package com.watermelon.model.entity;

import java.util.ArrayList;
import java.util.List;

public class LocationResult {
    private int id;
    private String location;
    private String weekday;
    private String date;
    private List<PrizeResult> prizes;
    private String region;

    public LocationResult(int id, String location, String weekday, String date, String region) {
        this.id = id;
        this.location = location;
        this.weekday = weekday;
        this.date = date;
        this.region = region;
        this.prizes = new ArrayList<>();
    }

    // Getters and setters...

    public void addPrize(String prizeName, List<String> result) {
        this.prizes.add(new PrizeResult(prizeName, result));
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getWeekday() {
		return weekday;
	}

	public void setWeekday(String weekday) {
		this.weekday = weekday;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public List<PrizeResult> getPrizes() {
		return prizes;
	}

	public void setPrizes(List<PrizeResult> prizes) {
		this.prizes = prizes;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}
    
}

