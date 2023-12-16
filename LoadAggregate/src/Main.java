import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.constant.Constable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
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

	public void load(int id,String dateRun) throws Exception {
		// 1.Load file control init
		properties = new Properties();
	    try (InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("config.properties")) {
	        if (inputStream == null) {
	            throw new FileNotFoundException("config.properties not found in the classpath");
	        }
	        properties.load(inputStream);
	        serverName = properties.getProperty("SERVER_NAME");
			port = properties.getProperty("PORT");
			userName = properties.getProperty("USER_NAME");
			pass = properties.getProperty("PASS");	
			dbName = properties.getProperty("DB_NAME");
			location_log = properties.getProperty("LOCATION_LOG");
			System.out.println(pass);
	    }
		
		
		// 2.Connect database control
		conectDB = new ConectDB();
	
		try (Connection connection = conectDB.getConnection(serverName, port, dbName, userName, pass)) {
			System.out.println(conectDB.getConnection(serverName, port, dbName, userName, pass));
			// Nếu thành công = yes
			// 3. Load các dòng config có flag=1 và id= id và dòng data_files mới nhất có ngày tương ứng    			
			String callProcedureInsertData = "{CALL loadConfig(?,?)}";
			CallableStatement callableStatement2 = connection.prepareCall(callProcedureInsertData);			
			callableStatement2.setInt(1, id);
			callableStatement2.setString(2, dateRun);
			ResultSet resultSet = callableStatement2.executeQuery();
			while (resultSet.next()) {
				// 4. Lấy ra 1 dòng				
				String status = resultSet.getString("status");
				String id_config = resultSet.getString("id_config");
				String location = resultSet.getString("location");
				String fileName = resultSet.getString("fileName");
				String seperator = resultSet.getString("seperator");
				String format = resultSet.getString("format");
				String databaseNameDatawarehouse = resultSet.getString("databaseNameDatawarehouse");
				String serverName = resultSet.getString("serverName");
				String port = resultSet.getString("port");
				String user = resultSet.getString("user");
				String pass = resultSet.getString("pass");
									
				// Kiểm tra status = null || BE||CE||BL|| CL|| BT || CT || BLA  
				if (status.equals("null") || status.equals("BE") || status.equals("CE") || status.equals("BL") || status.equals("CL") || status.equals("BT") || status.equals("CT")) {
					// Trường hợp yes				
						// Kiểm tra status = CT || BLA
					if (status.equals("CT") || status.equals("BLA")) {// trường hợp Yes					
						// 5. Connect database Datawarehouse					
						try (Connection connectionDatawarehouse = conectDB.getConnection(serverName, port, databaseNameDatawarehouse,
								user, pass)) {
							//  Thành công = yes
							// 6. insert into table data_file với status = BLA
								inserDataFile(id_config, dateRun, "BLA", "Bắt đầu load Aggregate", "LoadAggregate", connection);								
								
								// 7. Sử dụng câu lênh load data into để load tất tất cả dữ liệu vào bảng tạm
								String callProcedureLoad = "call TruncateDataAggregate()";
								try (CallableStatement statementLoad = connectionDatawarehouse.prepareCall(callProcedureLoad)) {								    
								    int rs = statementLoad.executeUpdate();
								// Thành công ? Yes
								// 8.Sử câu lệnh gọi proceduce loadAggregate để chuyển dữ liệu từ datawarehouse vào aggregate 
								String callProcedure = "{CALL InsertResultByDaySouthAggregates()}";
								try (CallableStatement callableStatement = connectionDatawarehouse.prepareCall(callProcedure)) {
									int rsUpdateNull = callableStatement.executeUpdate();
									// Thành công ? Yes
									// 9 .insert into table data_file với status = CLA
									inserDataFile(id_config,dateRun, "CLA", "Đã load vào aggregate thành công", "LoadAggregate",
											connection);
//									11 Đóng connect	
									connectionDatawarehouse.close();
							        connection.close();							     
							        return;													
								}
							} catch (Exception e) {
								// Trường hợp Thành công? = no
//								10 insert into table data_file với status = FLA								
								inserDataFile(id_config,dateRun, "FLA", e.getMessage(), "Load Aggregate", connection);
//								  11 .Đóng connect	
									connection.close();
									return;
							}
						} catch (Exception e) {
							// Connect database datawarehose Trường hợp Thành công? = no
//							10 insert into table data_file với status = FLA								
							inserDataFile(id_config,dateRun, "FLA", e.getMessage(), "Load Aggregate", connection);
//							  11 .Đóng connect	
								connection.close();
								return;
						}

					}else {// Kiểm tra status = CT || BLA trường hợp  là no										
//						10 insert into table data_file với status = FLA								
						inserDataFile(id_config,dateRun, "FLA", "Không lload được do chưa hoàn thành các module trước", "Load Aggregate", connection);
//						  11 .Đóng connect	
							connection.close();
							return;
					}
				}	
			}			
		}
		//Connect database control trường hợp thành công là no
		catch (Exception e) {
			// 12. Ghi log vào file D://log
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
		
		int id_source = args.length > 0 ? Integer.parseInt(args[0]) : 1;
		String dateRun = args.length > 1 ? args[1] : String.valueOf(LocalDate.now());
		new Main().load(id_source,dateRun);
	
	}
}
