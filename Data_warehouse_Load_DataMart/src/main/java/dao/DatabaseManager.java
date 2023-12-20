package dao;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;

import utils.Const;
import utils.IO;

public class DatabaseManager {

	// 2 Kết nối CSDL control
	private static final Jdbi controlJdbi = createJdbi(EDatabase.CONTROL.toString().toLowerCase());
	private static Jdbi datawarehouseJdbi;
	private static Jdbi datamartJdbi;

	public static Jdbi createJdbi(String dbName) {
		Properties properties = loadProperties(dbName);
		return Jdbi.create(properties.getProperty("database.url"), properties.getProperty("database.user"),
				properties.getProperty("database.password"));
	}

	public static Jdbi createJdbi(Properties properties) {
		return Jdbi.create(properties.getProperty("database.url"), properties.getProperty("database.user"),
				properties.getProperty("database.password"));
	}

	public static Jdbi createJdbi(String url, String user, String password) {
		return Jdbi.create(url, user, password);
	}

	// 1. load dữ liệu từ file database.properties

	public static Properties loadProperties(String dbName) {
		Properties prop = new Properties();
		try (InputStream input = DatabaseManager.class.getClassLoader().getResourceAsStream("database.properties")) {
			prop.load(input);
			// Thay đổi key để phản ánh tên cơ sở dữ liệu (control, datawarehouse, datamart)
			prop.setProperty("database.url", prop.getProperty(dbName + ".database.url"));
			prop.setProperty("database.user", prop.getProperty(dbName + ".database.user"));
			prop.setProperty("database.password", prop.getProperty(dbName + ".database.password"));
			System.out.println(prop);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return prop;
	}

	public static Jdbi getControlJdbi() {
		return controlJdbi;
	}

	public static Jdbi connectDatawarehouse(String server, String port, String name, String user, String password) {
		String url = "jdbc:mysql://" + server + ":" + port + "/" + name;
		System.out.println(url);
		datawarehouseJdbi = createJdbi(url, user, password);
		return datawarehouseJdbi;
	}

	public static Jdbi connectDatamartJdbi(String server, String port, String name, String user, String password) {
		String url = "jdbc:mysql://" + server + ":" + port + "/" + name;
		System.out.println(url);
		datamartJdbi = createJdbi(url, user, password);
		return datamartJdbi;
	}

	public static Jdbi getDatawarehouseJdbi() {
		return datawarehouseJdbi;
	}
	

	public static Jdbi getDatamartJdbi() {
		return datamartJdbi;
	}

	 public static boolean isControlDatabaseConnected() {
	        try (Handle handle = controlJdbi.open()) {
	            // Thực hiện một truy vấn đơn giản để kiểm tra kết nối
	            return true;
	        } catch (Exception e) {
	        	Date currentDateTime = new Date();
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
				String formattedDateTime = dateFormat.format(currentDateTime);
				IO.createFileWithIncrementedName(Const.LOCAL_LOG, "Log_error_" + formattedDateTime + ".txt",
						"Modul : Load_Datamart | Error : không kết nối được database control | Reason: "+ e.getMessage() );
	            return false;
	        }
	    }
	
}
