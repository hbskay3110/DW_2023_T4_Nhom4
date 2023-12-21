package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.StringTokenizer;

import com.mysql.cj.protocol.a.LocalDateValueEncoder;

import model.DataConfig;
import utils.Const;
import utils.IO;

public class DaoControl {

	// Hàm thực hiện các truy vấn cập nhật (INSERT, UPDATE, DELETE)
	private int executeUpdate(String query, Object... params) {
		int result = 0;
		Connection connection = null;
		// Mở và đóng kết nối tự động bằng try-with-resources
		try {
			connection = DataSource.getConnection();
			if (connection != null) {
				// Thực thi truy vấn cập nhật
				try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
					setParameters(preparedStatement, params);
					result = preparedStatement.executeUpdate();
				} catch (SQLException e) {
					System.out.println("Query execution error: " + e.getMessage());
				}
			}
		} catch (Exception e) {
			System.out.println("Connection error: " + e.getMessage());
			
		}
		finally {
			if (connection == null) {
				/*
				 * 3.1 .Ghi log vào file D://log//Log_error_yyyy-mm-dd hh-mm-ss.txt
				 */
				Date currentDateTime = new Date();
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
				String formattedDateTime = dateFormat.format(currentDateTime);
				IO.createFileWithIncrementedName(Const.LOCAL_LOG, "Log_error_" + formattedDateTime + ".txt",
						"Modul : Extract /n Error : không kết nối được database control");
			}
			try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return result;
	}

	// Hàm thực hiện các truy vấn truy vấn SELECT và ánh xạ kết quả với mapper
	private <T> T executeQuery(String query, ResultSetMapper<T> mapper, Object... params) {
		T result = null;
		Connection connection = null;
		/*
		 * 3.Kiểm tra kết nối cơ sở dữ liệu thành công hay không
		 */
		// Mở và đóng kết nối tự động bằng try-with-resources
		try {
			connection = DataSource.getConnection();
			if (connection != null) {
				// Thực thi truy vấn SELECT và ánh xạ kết quả
				try (PreparedStatement preparedStatement = connection.prepareStatement(query);
						ResultSet resultSet = setParametersAndExecuteQuery(preparedStatement, params)) {

					result = mapper.map(resultSet);
				} catch (SQLException e) {
					System.out.println("Query execution error: " + e.getMessage());

				}
			}

		} catch (Exception e) {
			System.out.println("Connection error: " + e.getMessage());

		} 
		finally {
			/*
			 * 3.1 .Ghi log vào file D://log//Log_error_yyyy-mm-dd hh-mm-ss.txt
			 */
			if (connection == null) {
				
				Date currentDateTime = new Date();
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
				String formattedDateTime = dateFormat.format(currentDateTime);
				IO.createFileWithIncrementedName(Const.LOCAL_LOG, "Log_error_" + formattedDateTime + ".txt",
						"Modul : Extract /n Error : không kết nối được database control");
			}
			try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return result;
	}

	// Hàm thiết lập các tham số cho PreparedStatement
	private void setParameters(PreparedStatement preparedStatement, Object... params) throws SQLException {
		for (int i = 0; i < params.length; i++) {
			preparedStatement.setObject(i + 1, params[i]);
		}
	}

	// Hàm thiết lập các tham số cho PreparedStatement và thực hiện truy vấn SELECT
	private ResultSet setParametersAndExecuteQuery(PreparedStatement preparedStatement, Object... params)
			throws SQLException {
		setParameters(preparedStatement, params);
		return preparedStatement.executeQuery();
	}

	// Phương thức thêm mới trạng thái vào bảng data_files
	public int addStatus(int id, String note, String status) {
		StringTokenizer tokenizer = new StringTokenizer(Const.date.isEmpty() ? LocalDate.now().toString() : Const.date,
				"-");
		int day = Integer.parseInt(tokenizer.nextToken());
		int month = Integer.parseInt(tokenizer.nextToken());
		int year = Integer.parseInt(tokenizer.nextToken());
		LocalDate date = Const.date.isEmpty() ? LocalDate.now() : LocalDate.of(year, month, day);
		String query = "INSERT INTO data_files (id_config, note, status,dateRun,created_by_modul) VALUES (?, ?, ?, ?, ?)";
		return executeUpdate(query, id, note, status, date, Const.NAME_PROCESS);
	}

	/*
	 * 
	 * 6.Lấy status từ 1 dòng trong bảng data_files với điều kiện: id bằng id nhận
	 * từ arguments[0] , thời gian tạo có phải là ngày hôm nay và status = "CE"
	 * 
	 */
	public String getStatusToday(int id) {
		String query = "SELECT F.status FROM data_configs AS C " + "JOIN data_files AS F ON F.id_config = ? "
				+ "WHERE F.status = 'CE' AND YEAR(F.dateRun) = YEAR(NOW()) "
				+ "AND MONTH(F.dateRun) = MONTH(NOW()) AND DAY(F.dateRun) = DAY(NOW()) " + "LIMIT 1";
		return executeQuery(query, rs -> {
			if (rs.next()) {
				return rs.getString("status");
			}
			return null;
		}, id);
	}

	/*
	 * 4.Lấy 1 dòng trong bảng data_configs theo id và có flat = 1
	 */
	// Phương thức lấy thông tin cấu hình từ bảng data_configs
	public DataConfig getDataConfig(int id) {
		String query = "SELECT * FROM data_configs AS C WHERE C.flag = 1 AND C.id = ? LIMIT 1";
		return executeQuery(query, rs -> {
			DataConfig dataConfig = new DataConfig();
			if (rs.next()) {
				dataConfig.setId(rs.getInt("id"));
				dataConfig.setFlag(rs.getInt("flag"));
				dataConfig.setCode(rs.getString("code"));
				dataConfig.setSourcePath(rs.getString("source_path"));
				dataConfig.setLocation(rs.getString("location"));
				dataConfig.setFileName(rs.getString("fileName"));
				dataConfig.setFormat(rs.getString("format"));
				dataConfig.setSeparation(rs.getString("seperator"));
			}
			return dataConfig;
		}, id);
	}

	// Giao diện ánh xạ ResultSet thành một đối tượng T
	private interface ResultSetMapper<T> {
		T map(ResultSet rs) throws SQLException;
	}

	public String getStatusByDate(int id, String date) {
		StringTokenizer tokenizer = new StringTokenizer(date, "-");
		String day = tokenizer.nextToken();
		String month = tokenizer.nextToken();
		String year = tokenizer.nextToken();
		String query = "SELECT F.status FROM data_configs AS C " + "JOIN data_files AS F ON F.id_config = ? "
				+ "WHERE F.status = 'CE' AND YEAR(F.dateRun) = ? " + "AND MONTH(F.dateRun) = ? AND DAY(F.dateRun) = ? "
				+ "LIMIT 1";
		return executeQuery(query, rs -> {
			if (rs.next()) {
				return rs.getString("status");
			}
			return null;
		}, id, year, month, day);
	}

}