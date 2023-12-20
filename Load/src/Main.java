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
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
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
		
			// Nếu thành công
			// 3. Load dòng config có flag=1 và id= id và dòng data_files mới nhất tương ứng bằng procedure loadConfig(id,dateRun)        
			
			String callProcedureInsertData = "{CALL loadConfig(?,?)}";
			CallableStatement callableStatement2 = connection.prepareCall(callProcedureInsertData);
			System.out.println(2);
			callableStatement2.setInt(1, id);
			callableStatement2.setString(2, dateRun);
			ResultSet resultSet = callableStatement2.executeQuery();
			while (resultSet.next()) {
				// 4. Lấy ra 1 dòng				
				String status = resultSet.getString("status");			
				// Kiểm tra status != null || BE||CE||BL
				if (status.equals("null") || status.equals("BE") || status.equals("CE") || status.equals("BL")) {
					// yes
				
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
						try (Connection connectionStaging = conectDB.getConnection(serverName, port, databaseNameStaging,
								user, pass)) {
							//  Thành công = yes
							// 8. insert into table data_file với status = BL bẳng procedure insertDataFile( id, dateRun, status,note ,created_by_)
								inserDataFile(id_config, dateRun, "BL", "Bắt đầu load", "Load", connection);								
								// 9. Sử dụng câu lênh load data into để load tất tất cả dữ liệu vào bảng tạm
								String callProcedureLoad = "LOAD DATA INFILE ? INTO TABLE " + tableNameStagingTemp + " CHARACTER SET utf8 FIELDS TERMINATED BY ? LINES TERMINATED BY '\n' IGNORE 1 ROWS";

								try (PreparedStatement statementLoad = connectionStaging.prepareStatement(callProcedureLoad)) {
								    String fullLocation = location + fileName + "." + format;

								    statementLoad.setString(1, fullLocation);
								    statementLoad.setString(2, seperator);
								    int rs = statementLoad.executeUpdate();

								// Thành công ? Yes
//								10 Xử lý dữ liệu  bị thiếu trong table staging bằng cách thay bằng giá trị default bằng proceduce update_null
								String callProcedure = "{CALL update_null()}";
								try (CallableStatement callableStatement = connectionStaging.prepareCall(callProcedure)) {
									int rsUpdateNull = callableStatement.executeUpdate();

									// 11 . Chuyển dữ liệu từ bảng tạm qua bảng staging
									String callProcedureMoveTempToStaging = "{CALL moveTempToStaging(?)}";
									try (CallableStatement callableStatement1 = connectionStaging
											.prepareCall(callProcedureMoveTempToStaging)) {
										callableStatement1.setInt(1, id);
										int rsMoveTempToStaging = callableStatement1.executeUpdate();							
										// yes
										// 12 .insert into table data_file với status = CL
										inserDataFile(id_config,dateRun, "CL", "Đã load vào staging thành công", "Load",
												connection);
										  File fileCanXoa = new File(fullLocation);
									        // Kiểm tra file D://data//data.csv có tồn tại không
									        if (fileCanXoa.exists()) { 
									        	// 15 Xóa file csv
									             fileCanXoa.delete();
									        }
//									        14 Đóng connect									        
									        connection.close();
									        connectionStaging.close();
									        return;
									}
								}
							} catch (Exception e) {
								// 13 insert into table data_file với status = FL
								System.out.println(e.getMessage());
								inserDataFile(id_config,dateRun, "FL", e.getMessage(), "Load", connection);
							}
						} catch (Exception e) {
							// no
							// 13. 13 insert into table data_file với status = FL bằng proceduce insertDataFile( id, dateRun, status,note ,created_by_)						
						
							inserDataFile(id_config,dateRun, "FL", "Load Error", "Load", connection);
//							  14 .Đóng connect	
								connection.close();
								return;
						}

					}else {// Trường hợp kiểm tra status = CE  là no										
							//  Status = FE hay status = null là yes
							if(status.equals("FE") || status == null) {
								// 13. 13 insert into table data_file với status = FL bằng proceduce insertDataFile( id, dateRun, status,note ,created_by_)
								inserDataFile(String.valueOf(id),dateRun, "FL", "Load Error", "Load", connection);								
//								  14 .Đóng connect	
								connection.close();
								return;
							}	
					}
				}
//				  14 .Đóng connect	
					connection.close();
			}			
		}
		// Không thành công
		catch (Exception e) {
			// 15. Ghi log vào file D://log//Log_error_i.txt
			  Date currentDateTime = new Date();
		        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
		        String formattedDateTime = dateFormat.format(currentDateTime);
			createFileWithIncrementedName(location_log, "Log_error_"+formattedDateTime+".txt", "Modul : Load /n Error : " + e.getMessage());
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


	private static void createFileWithIncrementedName(String filePath, String fileName, String data) {
	    Path path = Paths.get(filePath, fileName);
	    
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
		new Main().load(1,"2023-12-15");
//		new Main().load(id_source,dateRun);
	}
}
