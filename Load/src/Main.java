import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.sql.CallableStatement;

import ConectDB.ConectDB;

public class Main {
	String serverName = "";
	String dbName = "";
	String port = "";
	String userName = "";
	String pass = "";
	String location_log = "";
	Properties properties;
	ConectDB conectDB;

	public void load() throws Exception {
		// 1.Load file control init
		properties = new Properties();
		FileInputStream fileInputStream = new FileInputStream("config.properties");
		properties.load(fileInputStream);
		serverName = properties.getProperty("SERVER_NAME");
		port = properties.getProperty("PORT");
		userName = properties.getProperty("USER_NAME");
		pass = properties.getProperty("PASS");
		dbName = properties.getProperty("DB_NAME");
		location_log = properties.getProperty("LOCATION_LOG");
		// 2.Connect database control
		conectDB = new ConectDB();

		try (Connection connection = conectDB.getConnection(serverName, port, dbName, userName, pass)) {
			// Nếu thành công
			// 3.Load các dòng config có flag=1 và dòng data_files mới nhất tương ứng
			String ngaychay = "2023-11-23";
			String query = "SELECT c.*, d.* FROM data_configs c JOIN data_files d ON c.id = d.id_config WHERE c.flag = 1 and (d.id_config, d.create_at) IN \r\n"
					+ "(SELECT id_config, MAX(create_at) FROM data_files GROUP BY id_config)";
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				// 4. Lấy ra 1 dòng
				String status = resultSet.getString("status");
				// Kiểm tra status != null || BE||CE||BL
				if (!status.equals("null") || !status.equals("BE") || !status.equals("CE") || !status.equals("BL")) {
					// yes
					connection.close();
					return;
				} else {// no
						// /kiểm tra status có bằng CE không
					if (status.equals("CE")) {// có
						// 6. Lấy các thông số từ table config để đọc dữ liệu file csv
						String id_config = resultSet.getString("id_config");
						String location = resultSet.getString("location");
						String fileName = resultSet.getString("fileName");
						String seperator = resultSet.getString("seperator");
						String format = resultSet.getString("format");
						String databaseNameStaging = resultSet.getString("databaseNameStaging");
						String serverName = resultSet.getString("serverName");
						String port = resultSet.getString("port");
						String user = resultSet.getString("user");
						String pass = resultSet.getString("pass");
						String tableNameStagingTemp = resultSet.getString("tableNameStagingTemp");
						String columnsStagingTemp = resultSet.getString("columnsStagingTemp");
						String TypeColumnsStagingTemp = resultSet.getString("TypeColumnsStagingTemp");
						// 7. Connect database staging
						Connection connectionStaging = conectDB.getConnection(serverName, port, databaseNameStaging,
								user, pass);
						if (conectDB.checkConnection(connectionStaging)) {
							// yes
							// 8. insert into table data_file với status = BL
							String sql = "INSERT INTO data_files ( id_config, note, status,dateRun, created_by_modul) VALUES"
									+ "(" + id_config + ", 'Begin load', 'BL', '" + ngaychay + "', 'LOAD'), ";
							// 9. Sử dụng câu lênh load data into để load tất tất cả dữ liệu vào bảng tạm
							String callProcedureLoad = "{CALL loadDataFromFile(?,?,?)}";
							try (CallableStatement callableStatementLoad = connection.prepareCall(callProcedureLoad)){
								String fullLocation = location+fileName+"."+format;
								callableStatementLoad.setString(1, fullLocation);
								callableStatementLoad.setString(2, seperator);
								callableStatementLoad.setString(3, tableNameStagingTemp);
								ResultSet rs = callableStatementLoad.executeQuery();
								// Thành công ? Yes
								// 10 Xử lý dữ liệu bị thiếu trong table staging bằng cách thay bằng giá trị
								// default
								String callProcedure = "{CALL update_null()}";
								try (CallableStatement callableStatement = connection.prepareCall(callProcedure)) {
									ResultSet rsUpdateNull = callableStatement.executeQuery();

									// 11 . Chuyển dữ liệu từ bảng tạm qua bảng staging
									String callProcedureMoveTempToStaging = "{CALL moveTempToStaging()}";
									try (CallableStatement callableStatement1 = connection
											.prepareCall(callProcedureMoveTempToStaging)) {
										ResultSet rsMoveTempToStaging = callableStatement1.executeQuery();
										// 12 .insert into table data_file với status = CL
										inserDataFile(ngaychay, "CL", "Đã load vào staging thành công", "Load",
												connection);

									}
								}
							} catch (Exception e) {
								// 13 insert into table data_file với status = FL
								inserDataFile(ngaychay, "FL", e.getMessage(), "Load", connection);
							}
						} else {
							// no
							// 13. insert into table data_file với status = FL
							inserDataFile(ngaychay, "FL", "Load Error", "Load", connection);
						}

					}else {// Trường hợp kiểm tra status = CE  là no				
						
						while(true) {
							//  Status = FE hay status = null là yes
							if(status.equals("FE") || status == null) {
								// 13. insert into table data_file với status = FL
								inserDataFile(ngaychay, "FL", "Load Error", "Load", connection);
								break;
							}else {
							//  Status = FE hay status = null là no
								 // Chờ 1 phút
								  TimeUnit.MINUTES.sleep(1);	
							}
						}
						
					}
				}

			}

//				  14 .Đóng connect		
			connection.close();
		}
		// Không thành công
		catch (Exception e) {
			// 15. Ghi log vào file D://log
			createFileWithIncrementedName(location_log, "Log_error.txt", "Modul : Load /n Error : " + e.getMessage());
		}
	}

	public void inserDataFile(String ngaychay, String status, String mess, String modul, Connection connection)
			throws SQLException {
		String callProcedureInsertData = "{CALL insertDataFile(?,?,?,?)}";
		CallableStatement callableStatement = connection.prepareCall(callProcedureInsertData);

		callableStatement.setString(1, ngaychay);
		callableStatement.setString(2, status);
		callableStatement.setString(3, mess);
		callableStatement.setString(4, modul);
		ResultSet rs = callableStatement.executeQuery();
	}

	private static String incrementFileName(String fileName, int number) {
		// Thêm số vào tên file trước phần mở rộng
		int dotIndex = fileName.lastIndexOf(".");
		String baseName = fileName.substring(0, dotIndex);
		String extension = fileName.substring(dotIndex);
		return baseName + "_" + number + extension;
	}

	private static void createFileWithIncrementedName(String filePath, String fileName, String data) {
		Path path = Paths.get(filePath, fileName);

		// Kiểm tra xem tệp có tồn tại hay không
		int fileNumber = 1;
		while (Files.exists(path)) {
			// Tăng số và thêm vào tên file
			fileName = incrementFileName(fileName, fileNumber);
			path = Paths.get(filePath, fileName);
			fileNumber++;
		}

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile()))) {
			// Ghi dữ liệu vào file
			writer.write(data);

			System.out.println("Dữ liệu đã được ghi vào file '" + fileName + "' thành công.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		new Main().load();

	}
}
