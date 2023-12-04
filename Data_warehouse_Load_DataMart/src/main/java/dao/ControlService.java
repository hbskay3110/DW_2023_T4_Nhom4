package dao;

import org.jdbi.v3.core.Jdbi;

public class ControlService {
	private final Jdbi controlJdbi = DatabaseManager.getControlJdbi();

    public String getStatusToday(int id) {
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
    
    // Phương thức thêm mới trạng thái vào bảng data_files
    public int addStatus(int id, String note, String status) {
        return controlJdbi.withHandle(handle -> {
            return handle.createUpdate("INSERT INTO data_files (id_config, note, status) VALUES (:id, :note, :status)")
                .bind("id", id)
                .bind("note", note)
                .bind("status", status)
                .execute();
        });
    }
 // Phương thức lấy thông tin cấu hình dữ liệu theo ID
    public DataConfig getDataConfig(int id) {
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