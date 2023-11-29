package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.DataConfig;
import utils.Const;

public class DaoControl {
	
	/*
	 * hàm  addStatus(int id, String note ,String status)
	 * Chức năng: thêm trạng thái 
	 * */
	public int addStatus(int id, String note ,String status) {
		int result = 0;
		try (Connection connection = DataSource.getConnection()) {
            if (connection != null) {
                String query = "INSERT INTO data_files ( id_config, note, status)"
                		+ " VALUES (?,?,?)" ;
                try {
                	PreparedStatement preparedStatement = connection.prepareStatement(query);
                		preparedStatement.setInt(1, id);
                		preparedStatement.setString(2, note);
                		preparedStatement.setString(3, status);
                     result = preparedStatement.executeUpdate();

               return result;

                } catch (SQLException e) {
                    System.out.println("Query execution error: " + e.getMessage());
                }

            }
        } catch (SQLException e) {
            System.out.println("Connection error: " + e.getMessage());
        }
		 return result;
	}
	
	/*
	 * hàm getStatusToday()
	 * Lấy ra trạng thái ngày hôm nay
	 * */
	public String getStatusToday() {
		String status= "";
		try (Connection connection = DataSource.getConnection()) {
            if (connection != null) {
                // Example query execution
                String query = "SELECT F.status FROM data_configs AS C \r\n"
                		+ "JOIN data_files AS F ON C.id = F.id_config\r\n"
                		+ " WHERE F.status = 'CE' AND YEAR(F.created_at) = YEAR(NOW())\r\n"
                		+ " AND MONTH(F.created_at) = MONTH(NOW()) AND DAY(F.created_at) = DAY(NOW())"
                		+ " AND C.flag = 1"
                		+ " LIMIT 1"
                		;
                try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                     ResultSet resultSet = preparedStatement.executeQuery()) {

                    while (resultSet.next()) {
                    	status = resultSet.getString("status");
                    }
                    return status;

                } catch (SQLException e) {
                    System.out.println("Query execution error: " + e.getMessage());
                }
               

            }
        } catch (SQLException e) {
            System.out.println("Connection error: " + e.getMessage());
        }
		return status;
		
	}
	
	/*
	 * hàm getDataConfig(String code , int id)
	 * Nhận vào code và id để load lên thuộc tính cấu hình tương ứng
	 * */
	public  DataConfig getDataConfig(int id) {
		DataConfig rs = null;
		try (Connection connection = DataSource.getConnection()) {
            if (connection != null) {
                // Example query execution
                String query = "SELECT * FROM data_configs AS C"
                		+ " WHERE C.flag = 1 AND C.id = ?"
                		+ " LIMIT 1"
                		;
                try {
                	PreparedStatement preparedStatement = connection.prepareStatement(query);
                		preparedStatement.setInt(1, id);
                		ResultSet resultSet = preparedStatement.executeQuery();
                	rs = new DataConfig();
                    while (resultSet.next()) {
                    	
                    rs.setId(resultSet.getInt("id"));
                    rs.setFlag(resultSet.getInt("flag"));
                    rs.setCode(resultSet.getString("code"));
                    rs.setSourcePath(resultSet.getString("source_path"));
                    rs.setLocation(resultSet.getString("location"));
                    rs.setFileName(resultSet.getString("file_name"));
                    rs.setFormat(resultSet.getString("format"));
                    rs.setSeparation(resultSet.getString("separation"));
                    }
                    return rs;

                } catch (SQLException e) {
                    System.out.println("Query execution error: " + e.getMessage());
                }
               

            }
        } catch (SQLException e) {
            System.out.println("Connection error: " + e.getMessage());
        }
		return rs;
	}
	

}
