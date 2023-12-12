package com.watermelon.model.entity;

import java.util.List;

public class PrizeResult {

	private String prizeName;
    private List<String> result;

    public PrizeResult(String prizeName, List<String> result) {
        this.prizeName = prizeName;
        this.result = result;
    }

    public String getPrizeName() {
        return prizeName;
    }

    public List<String> getResult() {
        return result;
    }
}
