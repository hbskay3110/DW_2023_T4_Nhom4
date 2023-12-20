package db;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import utils.Const;
import utils.IO;

public class DataSource {

	private static HikariConfig config;
	private static HikariDataSource dataSource;

	static {
		/*
		 * 1. Load các cấu hình database từ file database.properties
		 */
		Properties properties = loadDatabaseProperties();

		config = new HikariConfig();
		config.setDriverClassName(properties.getProperty("database_driver"));
		config.setJdbcUrl(properties.getProperty("database_url"));
		config.setUsername(properties.getProperty("datasase_user"));
		config.setPassword(properties.getProperty("database_password"));
		config.setMaximumPoolSize(Integer.parseInt(properties.getProperty("MaximumPoolSize")));

		dataSource = new HikariDataSource(config);
	}

	private DataSource() {
	}

	/*
	 * 2. Kết nối database control
	 */
	public static Connection getConnection() throws SQLException {

			return dataSource.getConnection() != null ? dataSource.getConnection() : null;
	}

	private static Properties loadDatabaseProperties() {
		Properties properties = new Properties();

		try (InputStream input = Const.class.getClassLoader().getResourceAsStream("database.properties");
				InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
			properties.load(reader);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Lỗi khi đọc file properties.");
		}
		return properties;
	}

}
