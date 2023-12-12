package utils;

public enum EStatus {
	CLA("Complete Load Aggregate"),
	BLM("Begin Load Datamart"),
	FLM("Fail Load Datamart"),
	CLM("Complete Load Datamart");
	
	private final String note;

	private EStatus(String note) {
		this.note = note;
		// TODO Auto-generated constructor stub
	}
	public String getNote() {
		return note;
	}
	
	
	

}
