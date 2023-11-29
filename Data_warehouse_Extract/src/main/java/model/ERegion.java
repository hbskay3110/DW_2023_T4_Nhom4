package model;

public enum ERegion {
MIEN_BAC("/mien-bac","Miền Bắc")
,MIEN_TRUNG("/mien-trung","Miền Trung")
,MIEN_NAM("/mien-nam","Miền Nam");

    private final String code;
    private final String description;
	
ERegion(String code, String description) {
	this.code=code;
	this.description=description;
}

public String getCode() {
	return code;
}

public String getDescription() {
	return description;
}



	
}
