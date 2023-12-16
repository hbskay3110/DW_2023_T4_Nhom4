package ConectDB;


import java.sql.Statement;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class ConectDB {
	public Connection getConnection(String serverName,String port, String dbName,String username,String pass) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			String url = "jdbc:mysql://"+serverName+":"+port+"/"+dbName;
			Connection connection = DriverManager.getConnection(url, username, pass);
			return connection;
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}
	public boolean checkConnection(Connection connection) {

        if (connection != null) {
            try {
                // Kiểm tra nếu kết nối hợp lệ trong vòng 3 giây
                return connection.isValid(3);
            } catch (SQLException e) {
                e.printStackTrace();
            } 
            
        }
        return false;
    }
	Properties properties ;
	public static void main(String[] args) throws Exception {
		ConectDB c = new ConectDB();
		System.out.println(c.checkConnection(c.getConnection("127.0.0.1","3306","control","root","123456789")));
	}

}
 