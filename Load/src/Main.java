import java.io.BufferedWriter;
import java.io.File;
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

	public void load(int id) throws Exception {
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
			// 3. Load các dòng config có flag=1 và id= id và dòng data_files mới nhất tương ứng    
			System.out.println(3);
			String ngaychay = "2023-12-07";
			String callProcedureInsertData = "{CALL loadConfig(?)}";
			CallableStatement callableStatement2 = connection.prepareCall(callProcedureInsertData);

			callableStatement2.setInt(1, id);
			ResultSet resultSet = callableStatement2.executeQuery();
			while (resultSet.next()) {
				// 4. Lấy ra 1 dòng
				System.out.println(4);
				String status = resultSet.getString("status");
				System.out.println(status);
				// Kiểm tra status != null || BE||CE||BL
				if (status.equals("null") || status.equals("BE") || status.equals("CE") || status.equals("BL")) {
					// yes
					// no
					System.out.println(1);
						// /kiểm tra status có bằng CE không
					if (status.equals("CE") || status.equals("BL")) {// có
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
						;
						System.out.println(7);
						try (Connection connectionStaging = conectDB.getConnection(serverName, port, databaseNameStaging,
								user, pass)) {
							//  Thành công = yes
							// 8. insert into table data_file với status = BL
								inserDataFile(id_config, ngaychay, status, "Bắt đầu load", "Load", connection);
								System.out.println(8);
								// 9. Sử dụng câu lênh load data into để load tất tất cả dữ liệu vào bảng tạm
							String callProcedureLoad = "LOAD DATA INFILE 'D://data//data.csv' INTO TABLE Lotteries_temp FIELDS TERMINATED BY ',' LINES TERMINATED  BY '\n' IGNORE 1 LINES;";
							try (PreparedStatement statementLoad = connectionStaging.prepareStatement(callProcedureLoad)){
								String fullLocation = location+fileName+"."+format;
								System.out.println(fullLocation);
								System.out.println(tableNameStagingTemp);
								System.out.println(seperator);
//								statementLoad.setString(1, fullLocation);
//								statementLoad.setString(2, tableNameStagingTemp);
//								statementLoad.setString(3, seperator);
								System.out.println();
								int rs = statementLoad.executeUpdate();
								System.out.println( "rs " + rs);
								// Thành công ? Yes
								// 10 Xử lý dữ liệu bị thiếu trong table staging bằng cách thay bằng giá trị default
								String callProcedure = "{CALL update_null()}";
								try (CallableStatement callableStatement = connectionStaging.prepareCall(callProcedure)) {
									int rsUpdateNull = callableStatement.executeUpdate();

									// 11 . Chuyển dữ liệu từ bảng tạm qua bảng staging
									String callProcedureMoveTempToStaging = "{CALL moveTempToStaging()}";
									try (CallableStatement callableStatement1 = connectionStaging
											.prepareCall(callProcedureMoveTempToStaging)) {
										int rsMoveTempToStaging = callableStatement1.executeUpdate();
										System.out.println("r" + rsMoveTempToStaging);
										// yes
										// 12 .insert into table data_file với status = CL
										inserDataFile(id_config,ngaychay, "CL", "Đã load vào staging thành công", "Load",
												connection);
										  File fileCanXoa = new File(fullLocation);

									        // Kiểm tra file D://data//data.csv có tồn tại không
//									        if (fileCanXoa.exists()) { 
//									        	// 15 Xóa file csv
//									             fileCanXoa.delete();
//									        }
//									        14 Đóng connect
									        connectionStaging.close();
									}
								}
							} catch (Exception e) {
								// 13 insert into table data_file với status = FL
								System.out.println(e.getMessage());
								inserDataFile(id_config,ngaychay, "FL", e.getMessage(), "Load", connection);
							}
						} catch (Exception e) {
							// no
							// 13. insert into table data_file với status = FL
							System.out.println(13);
							System.out.println(e.getMessage());
							inserDataFile(id_config,ngaychay, "FL", "Load Error", "Load", connection);
						}

					}else {// Trường hợp kiểm tra status = CE  là no										
						System.out.println(13);
							//  Status = FE hay status = null là yes
							if(status.equals("FE") || status == null) {
								// 13. insert into table data_file với status = FL
								inserDataFile(String.valueOf(id),ngaychay, "FL", "Load Error", "Load", connection);								
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

	public void inserDataFile(String id_config,String ngaychay, String status, String mess, String modul, Connection connection)
			throws SQLException {
		String callProcedureInsertData = "{CALL insertDataFile(?,?,?,?,?)}";
		CallableStatement callableStatement = connection.prepareCall(callProcedureInsertData);
		callableStatement.setString(1, id_config);
		callableStatement.setString(2, ngaychay);
		callableStatement.setString(3, status);
		callableStatement.setString(4, mess);
		callableStatement.setString(5, modul);
		int rs = callableStatement.executeUpdate();
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
		new Main().load(1);
	}
}
