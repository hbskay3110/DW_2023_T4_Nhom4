package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class Const {
    public static final String PATH;
    public static final String NAME_FILE;
    public static final String EXCEL_EXTENSION;
    public static final String NAME_SHEET;
    public static final String DATE_FORMAT;

    public static final String SOURCE_1;
    public static final String MIEN_NAM;
    public static final String MIEN_TRUNG;
    public static final String MIEN_BAC;
    public static final String NAM;
    public static final String BAC;
    public static final String NAME_NORTH;
    public static final String TRUNG;

    static {
        Properties properties = loadProperties();
        
        PATH = properties.getProperty("PATH");
        NAME_FILE = properties.getProperty("NAME_FILE");
        EXCEL_EXTENSION = properties.getProperty("EXCEL_EXTENSION");
        NAME_SHEET = properties.getProperty("NAME_SHEET");
        DATE_FORMAT = properties.getProperty("DATE_FORMAT");

        SOURCE_1 = properties.getProperty("SOURCE_1");
        MIEN_NAM = properties.getProperty("MIEN_NAM");
        MIEN_TRUNG = properties.getProperty("MIEN_TRUNG");
        MIEN_BAC = properties.getProperty("MIEN_BAC");
        NAME_NORTH = "Miền Bắc";
        NAM = properties.getProperty("NAM");
        BAC = properties.getProperty("BAC");
        TRUNG = properties.getProperty("TRUNG");
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();

        try (InputStream input = new FileInputStream("src/main/resources/config.properties");
             InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {

            properties.load(reader);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Lỗi khi đọc file properties.");
        }

        return properties;
    }
}
