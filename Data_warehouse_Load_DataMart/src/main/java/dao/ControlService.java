package dao;

import java.time.LocalDate;

import org.jdbi.v3.core.Jdbi;

public class ControlService {
	private static final Jdbi controlJdbi = DatabaseManager.getControlJdbi();

	// 6. kiểm tra ngày hôm nay aggregate đã chạy chưa
    public static String getStatusAggregateToday(int id) {
        return controlJdbi.withHandle(handle -> {
            return handle.createQuery("SELECT F.status FROM data_configs AS C "
                    + "JOIN data_files AS F ON F.id_config = :id "
                    + "WHERE F.status = 'CLA' AND YEAR(F.created_at) = YEAR(NOW()) "
                    + "AND MONTH(F.created_at) = MONTH(NOW()) AND DAY(F.created_at) = DAY(NOW()) "
                    + "LIMIT 1")
                .bind("id", id)
                .mapTo(String.class)
                .findOne()
                .orElse(null);
        });
    }
 // 7. kiểm tra ngày hôm nay load data mart đã chạy chưa
    public static String getStatusLoadDataMartToday(int id) {
        return controlJdbi.withHandle(handle -> {
            return handle.createQuery("SELECT F.status FROM data_configs AS C "
                    + "JOIN data_files AS F ON F.id_config = :id "
                    + "WHERE F.status = 'CLM' AND YEAR(F.created_at) = YEAR(NOW()) "
                    + "AND MONTH(F.created_at) = MONTH(NOW()) AND DAY(F.created_at) = DAY(NOW()) "
                    + "LIMIT 1")
                .bind("id", id)
                .mapTo(String.class)
                .findOne()
                .orElse(null);
        });
    }
    
    // Phương thức thêm mới trạng thái vào bảng data_files
    public static int addStatus(int id, String note, String status) {
    	LocalDate date = LocalDate.now();
    	String module = "LOAD_DATAMART";
        return controlJdbi.withHandle(handle -> {
            return handle.createUpdate("INSERT INTO data_files (id_config, note, status,dateRun,created_by_modul) VALUES (:id, :note, :status,:dateRun,:created_by_modul)")
                .bind("id", id)
                .bind("note", note)
                .bind("status", status)
                .bind("dateRun", date)
                .bind("created_by_modul", module)
                .execute();
        });
    }
 //3 - 4. Phương thức lấy thông tin cấu hình dữ liệu theo ID
    public static DataConfig getDataConfig(int id) {
        String query = "SELECT * FROM data_configs AS C WHERE C.flag = 1 AND C.id = ? LIMIT 1";
        return controlJdbi.withHandle(handle -> {
            return handle.createQuery(query)
                .bind(0, id)
                .mapToBean(DataConfig.class)
                .findFirst()
                .orElse(null);
        });
    }
    
}