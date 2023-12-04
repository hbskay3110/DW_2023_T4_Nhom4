package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

public class DataMartService {
	private final Jdbi dataMartJdbi = DatabaseManager.getDatamartJdbi();
	private final Jdbi dataWarehouseJdbi = DatabaseManager.getDatawarehouseJdbi();

	private static class ColumnInfoMapper implements RowMapper<ColumnInfo> {
		@Override
		public ColumnInfo map(ResultSet rs, StatementContext ctx) throws SQLException {
			ColumnInfo columnInfo = new ColumnInfo();
			columnInfo.setColumnName(rs.getString("Field"));
			columnInfo.setColumnType(rs.getString("Type"));
			columnInfo.setColumnNull(rs.getString("Null").contains("NO") ? "NOT NULL" : "");
			columnInfo.setColumnKey(!rs.getString("Key").isEmpty() ? "PRIMARY KEY" : "");
			columnInfo.setColumnDefault(rs.getString("Default") == null ? "" : rs.getString("Default"));
			columnInfo.setColumnExtra(rs.getString("Extra"));
			return columnInfo;
		}
	}

	public boolean tableExists(String tableName, Handle handle) {
		try {
			// Kiểm tra xem bảng đã tồn tại hay không
			List<String> existingTables = handle.createQuery("SHOW TABLES LIKE :table").bind("table", tableName)
					.mapTo(String.class).list();
			return !existingTables.isEmpty();
		} catch (Exception e) {
			System.err.println("Error checking table existence: " + e.getMessage());
			return false;
		}
	}

	/*
	 * Sao chép bảng: thuộc tính và dữ liệu từ datawarehouse
	 */
	// Trong DataMartService
	public boolean copyTable(String tableName) {
		boolean result = false;
		// Lấy thông tin cột và kiểu dữ liệu từ DataWarehouse
		try (Handle handle = dataWarehouseJdbi.open()) {
			if (tableExists(tableName, handle)) {
				System.out.println("Table " + tableName + " already exists. Skipping creation.");
				return false;
			}
			List<ColumnInfo> columnInfoList = handle.createQuery("DESCRIBE " + tableName).map(new ColumnInfoMapper())
					.list();

			// Tạo câu truy vấn CREATE TABLE cho DataMart
			StringBuilder createTableQuery = new StringBuilder("CREATE TABLE :table (");
			for (ColumnInfo columnInfo : columnInfoList) {
				createTableQuery.append(columnInfo.getColumnName()).append(" ").append(columnInfo.getColumnType())
						.append(" ").append(columnInfo.getColumnNull()).append(" ").append(columnInfo.getColumnKey())
						.append(" ").append(columnInfo.getColumnDefault()).append(" ")
						.append(columnInfo.getColumnExtra()).append(", ");
			}
			createTableQuery.setLength(createTableQuery.length() - 2); // Xóa dấu phẩy cuối cùng
			createTableQuery.append(")");

			// Thực hiện truy vấn CREATE TABLE trong DataMart
			try (Handle martHandle = dataMartJdbi.open()) {
				result = martHandle.createUpdate(createTableQuery.toString())
				 .bind("table", tableName)
                 .execute()>0
                 ? true :false;
               return result;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<String> getColumnInfo(String table) {
		try (Handle handle = dataWarehouseJdbi.open()) {
			List<ColumnInfo> columnInfoList = handle.createQuery("DESCRIBE " + table).map(new ColumnInfoMapper())
					.list();

			// Now you can extract the column names and types
			List<String> columnInfoStrings = new ArrayList<>();
			for (ColumnInfo columnInfo : columnInfoList) {
				String columnInfoString = columnInfo.getColumnName() + " " + columnInfo.getColumnType();
				columnInfoStrings.add(columnInfoString);
			}
			return columnInfoStrings;

		} catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyList(); // Handle the exception gracefully
		}
	}

	public boolean copyData(String table) {
		boolean result = false;
		try (Handle sourceHandle = dataWarehouseJdbi.open(); Handle destinationHandle = dataMartJdbi.open()) {

			// Kiểm tra xem bảng nguồn tồn tại hay không
			if (!tableExists(table, sourceHandle)) {
				System.out.println("Source table " + table + " does not exist.");
				return result;
			}

			// Kiểm tra xem bảng đích đã tồn tại hay chưa
			if (!tableExists(table, destinationHandle)) {
				System.out.println("Destination table " + table + " does not exist.");
				return result;
			}
			String sourceTable = "datawarehouse." + table;
			String destinationTable = "datamart." + table;

			// Thực hiện lệnh sao chép dữ liệu
			String insertQuery = "INSERT INTO " + destinationTable + " SELECT * FROM " + sourceTable;

			result = destinationHandle.execute(insertQuery) > 0 ? true : false;
			System.out.println("Data copied from " + destinationTable + " to " + sourceTable + " successfully.");
			return result;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static void main(String[] args) {
//		// Thực hiện sao chép bảng từ DataWarehouse sang DataMart
		DataMartService dataMartService = new DataMartService();
		DataWarehouseService dataWarehouseService = new DataWarehouseService();
		for (String table : dataWarehouseService.getAggregateTables()) {
			String tableNameToCopy = table; // Thay thế bằng tên bảng thực tế
			dataMartService.copyTable(tableNameToCopy);
		}
		dataMartService.copyData("result_by_day_south_aggregates");

	}

	private static class ColumnInfo {
		private String columnName;
		private String columnType;
		private String columnNull;
		private String columnKey;
		private String columnDefault;
		private String columnExtra;

		public String getColumnName() {
			return columnName;
		}

		public void setColumnName(String columnName) {
			this.columnName = columnName;
		}

		public String getColumnType() {
			return columnType;
		}

		public void setColumnType(String columnType) {
			this.columnType = columnType;
		}

		public String getColumnNull() {
			return columnNull;
		}

		public void setColumnNull(String columnNull) {
			this.columnNull = columnNull;
		}

		public String getColumnKey() {
			return columnKey;
		}

		public void setColumnKey(String columnKey) {
			this.columnKey = columnKey;
		}

		public String getColumnDefault() {
			return columnDefault;
		}

		public void setColumnDefault(String columnDefault) {
			this.columnDefault = columnDefault;
		}

		public String getColumnExtra() {
			return columnExtra;
		}

		public void setColumnExtra(String columnExtra) {
			this.columnExtra = columnExtra;
		}
	}

}
