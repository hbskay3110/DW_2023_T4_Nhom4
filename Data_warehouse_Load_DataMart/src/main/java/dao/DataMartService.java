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

	// Tạo câu truy vấn CREATE TABLE từ danh sách thông tin cột
	private String generateCreateTableQuery(String tableName, List<ColumnInfo> columnInfoList) {
		StringBuilder createTableQuery = new StringBuilder("CREATE TABLE " + tableName + " (");
		for (ColumnInfo columnInfo : columnInfoList) {
			createTableQuery.append(columnInfo.getColumnName()).append(" ").append(columnInfo.getColumnType())
					.append(" ").append(columnInfo.getColumnNull()).append(" ").append(columnInfo.getColumnKey())
					.append(" ").append(columnInfo.getColumnDefault()).append(" ").append(columnInfo.getColumnExtra())
					.append(", ");
		}
		createTableQuery.setLength(createTableQuery.length() - 2); // Xóa dấu phẩy cuối cùng
		createTableQuery.append(")");
		return createTableQuery.toString();
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

	// hàm copy data từ bảng này sang bảng khác trong cùng db
	private boolean copyData(String sourceTable, String destinationTable, Handle destinationHandle) {

		boolean result = false;
		// Thực hiện lệnh sao chép dữ liệu
		String insertQuery = "INSERT INTO " + destinationTable + " SELECT * FROM " + sourceTable;
		result = destinationHandle.execute(insertQuery) > 0 ? true : false;
		return result;
	}

	// tạo bảng tạm ở data mart
	public String createTableTempFromMainTable(String table) {
		String tempTable = table + "_temps";
		try (Handle martHandle = dataMartJdbi.open()) {
			if (tableExists(tempTable, martHandle)) {
				System.out.println("Table " + tempTable + " already exists. Skipping creation.");
				return null;
			}
			List<ColumnInfo> columnInfoList = martHandle.createQuery("DESCRIBE " + table).map(new ColumnInfoMapper())
					.list();
			String createTableQuery = generateCreateTableQuery(tempTable, columnInfoList);

			martHandle.execute(createTableQuery);
			// Thực hiện truy vấn CREATE TABLE trong DataMart
			return tempTable;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// 10.1 copy data từ bảng chính ta bảng tạm
	public boolean copyToDataTableInMart(String sourceTable, String destinationTable) {
		boolean result = false;
		try (Handle destinationHandle = dataMartJdbi.open()) {
			result = this.copyData(sourceTable, destinationTable, destinationHandle);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;

	}

	// 10.2 Xóa dữ liệu trong bảng
	public void truncateTable(String table) {
		try (Handle handle = dataMartJdbi.open()) {
			handle.execute("TRUNCATE TABLE " + table);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * 11. tạo bảng Sao chép bảng: thuộc tính và dữ liệu từ datawarehouse
	 */
	public boolean copyTable(String tableName) {
		// Lấy thông tin cột và kiểu dữ liệu từ DataWarehouse
		try (Handle handle = dataWarehouseJdbi.open()) {
			if (tableExists(tableName, handle)) {
				System.out.println("Table " + tableName + " already exists. Skipping creation.");
				return false;
			}
			List<ColumnInfo> columnInfoList = handle.createQuery("DESCRIBE " + tableName).map(new ColumnInfoMapper())
					.list();

			// Tạo câu truy vấn CREATE TABLE cho DataMart
			String createTableQuery = generateCreateTableQuery(tableName, columnInfoList);

			// Thực hiện truy vấn CREATE TABLE trong DataMart
			try (Handle martHandle = dataMartJdbi.open()) {
				martHandle.execute(createTableQuery.toString());
				return true;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// 13 hàm copy dữ liệu từ bảng trong db datawarehouse sang bảng trong db
	// datamart
	public boolean copyDataFromDatawarehouseToDatamart(String table) {
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
//				if()

			String sourceTable = "datawarehouse." + table;
			String destinationTable = "datamart." + table;

			result = this.copyData(sourceTable, destinationTable, destinationHandle);
			System.out.println("Data copied from " + sourceTable + " to " + destinationTable + " successfully.");
			return result;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// 14
	// 14.1 chèn CLM và end
	// 14.2 copy data từ bảng chính ta bảng tạm
//		public boolean copyToDataTableInMart(String sourceTable, String destinationTable) {
//			boolean result = false;
//			try (Handle destinationHandle = dataMartJdbi.open()) {
//				result = this.copyData(sourceTable, destinationTable, destinationHandle);
//
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//
//			return result;
//
//		}
	// 14.3 truncate bảng tạm

//		private void truncateTable(String table) {
//			try (Handle handle = dataMartJdbi.open()) {
//				handle.execute("TRUNCATE TABLE " + table);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}

	public static void main(String[] args) {
//		// Thực hiện sao chép bảng từ DataWarehouse sang DataMart
		DataMartService dataMartService = new DataMartService();
		DataWarehouseService dataWarehouseService = new DataWarehouseService();
		for (String table : dataWarehouseService.getAggregateTables()) {
			String tableNameToCopy = table; // Thay thế bằng tên bảng thực tế
			dataMartService.copyTable(tableNameToCopy);
		}
		dataMartService.copyDataFromDatawarehouseToDatamart("result_by_day_south_aggregates");

		String tableCopy = dataMartService.createTableTempFromMainTable("result_by_day_south_aggregates");
		dataMartService.copyToDataTableInMart("result_by_day_south_aggregates", tableCopy);

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
