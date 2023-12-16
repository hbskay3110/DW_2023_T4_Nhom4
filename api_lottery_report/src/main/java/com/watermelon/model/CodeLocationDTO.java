package com.watermelon.model;

public class CodeLocationDTO {
	private String code;
    private String name;

    public CodeLocationDTO(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
