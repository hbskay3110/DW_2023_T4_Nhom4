package com.watermelon.model.entity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class LottoResult {
    private int id;
    private String location;
    private String weekday;
    private String date;
    private List<PrizeResult> prizes;
    private String region;

    public LottoResult() {
        // Default constructor for JPA
    }

    public LottoResult(int id, String location, String weekday, Date date, String prize, String result, String region) {
        this.id = id;
        this.location = location;
        this.weekday = weekday;
        this.date = new SimpleDateFormat("dd-MM-yyyy").format(date);
        this.prizes = parsePrizes(prize, result);
        this.region = region;
    }

    // Getters and setters...

    public static LottoResult fromEntity(LottoResultEntity entity) {
        return new LottoResult(
                entity.getId(),
                entity.getLocation(),
                entity.getWeekday(),
                entity.getDate(),
                entity.getPrize(),
                entity.getResult(),
                entity.getRegion()
        );
    }

    private List<PrizeResult> parsePrizes(String prize, String result) {
        // Split prize and result strings into lists of strings
//        Strp prizeList = List.of(prize.split("\\s+"));
        List<String> resultList = List.of(result.split("\\s+"));

        // Convert lists of strings to PrizeResult objects
        return resultList.stream()
                .map(prizeName -> new PrizeResult(prize, extractResults(prizeName, resultList)))
                .collect(Collectors.toList());
    }

    private List<String> extractResults(String prizeName, List<String> resultList) {
        // Extract results corresponding to the given prizeName
        return resultList.stream()
                .filter(result -> result.matches("\\d+"))
//                .map(Integer::parseInt)
                .collect(Collectors.toList());
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
