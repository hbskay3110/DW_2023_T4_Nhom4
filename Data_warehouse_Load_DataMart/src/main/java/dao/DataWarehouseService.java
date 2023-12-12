package dao;

import org.jdbi.v3.core.Jdbi;

import java.util.List;

public class DataWarehouseService {
    private final Jdbi dataWarehouseJdbi = DatabaseManager.getDatawarehouseJdbi();

    /*
     * Lấy ra danh sách các bảng có tên chứa từ khóa 'aggregate'
     */
 // Trong DataWarehouseService
    public List<String> getAggregateTables() {
        String keyword = "aggregates";

        return dataWarehouseJdbi.withHandle(handle -> {
            // Sử dụng truy vấn SQL để lấy danh sách các bảng có chứa từ khóa 'aggregate'
            return handle.createQuery("SHOW TABLES LIKE :keyword")
                .bind("keyword", "%" + keyword + "%")
                .mapTo(String.class)
                .list();
        });
    }

    public static void main(String[] args) {

        // Lấy danh sách các bảng chứa từ khóa 'aggregate'
        DataWarehouseService dataWarehouseService = new DataWarehouseService();
        List<String> aggregateTables = dataWarehouseService.getAggregateTables();

        // In danh sách các bảng
        System.out.println("Aggregate Tables:");
        for (String tableName : aggregateTables) {
            System.out.println(tableName);
        }
    }
}
