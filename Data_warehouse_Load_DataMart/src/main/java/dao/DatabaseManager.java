package dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.jdbi.v3.core.Jdbi;

public class DatabaseManager {
	private static final Jdbi controlJdbi = createJdbi(EDatabase.CONTROL.toString().toLowerCase());
    private static final Jdbi datawarehouseJdbi = createJdbi(EDatabase.DATAWAREHOUSE.toString().toLowerCase());
    private static final Jdbi datamartJdbi = createJdbi(EDatabase.DATAMART.toString().toLowerCase());

    private static Jdbi createJdbi(String dbName) {
        Properties properties = loadProperties(dbName);
        return Jdbi.create(
                properties.getProperty("database.url"),
                properties.getProperty("database.user"),
                properties.getProperty("database.password")
        );
    }
 
    // load dữ liệu từ file database.properties

    private static Properties loadProperties(String dbName) {
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

    public static Jdbi getDatawarehouseJdbi() {
        return datawarehouseJdbi;
    }

    public static Jdbi getDatamartJdbi() {
        return datamartJdbi;
    }
    
    
    public static void main(String[] args) {

        
        // Thực hiện một truy vấn từ ControlService
        ControlService controlService = new ControlService();
        String status = controlService.getStatusToday(1);
        System.out.println("Status today: " + status);
        
        
        // Lấy thông tin cấu hình dữ liệu theo ID
        int id = 1; // ID cần được thay thế bằng giá trị thực tế
        DataConfig dataConfig = controlService.getDataConfig(id);

        // Kiểm tra xem có dữ liệu trả về hay không
        if (dataConfig != null) {
            System.out.println("DataConfig information: " + dataConfig.toString());
        } else {
            System.out.println("DataConfig not found with ID: " + id);
        }
	}
    
}

