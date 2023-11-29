package utils;

import db.DaoControl;
import model.DataConfig;

public class Const {
	// file
	public static String PATH;
	public static String NAME_FILE;
	public static String CSV_EXTENSION;
	// 
	public static String SOURCE_1;
	public static final String NAME_SHEET = "data";
	public static final int QUANTITY_ATTRIBUTE = 6;


	
	public static String date;
	public static int idSource_1;
	static DataConfig config ;
	public static void loadConfFromDB() {
		config = new DaoControl().getDataConfig(idSource_1);
		PATH = config.getLocation();
		NAME_FILE = config.getFileName();
		CSV_EXTENSION = config.getFormat();
		SOURCE_1 = config.getSourcePath();
	}


	public static void setDate(String date) {
		Const.date = date;
	}

	public static void setIdSource_1(int idSource_1) {
		Const.idSource_1 = idSource_1;
	}
	

}
