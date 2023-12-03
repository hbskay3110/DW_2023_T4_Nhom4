package model;

public enum ENameFieldCollection {
	LOCATION(0,"locations"),
	WEEKDAYS(1,"weekdays"),
	DATE(2,"date"),
	PRIZES(3,"prizes"),
	RESULTS(4,"results"),
	REGIONS(5,"regions");
	
	private final int col;
	private final String nameColumn;
	private ENameFieldCollection(int col, String nameColumn) {
		this.col = col;
		this.nameColumn = nameColumn;
	}
	public int getCol() {
		return col;
	}
	public String getNameColumn() {
		return nameColumn;
	}
	

}
