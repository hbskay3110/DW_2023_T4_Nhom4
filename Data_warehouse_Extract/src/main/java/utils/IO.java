package utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class IO {
	public static void createFileWithIncrementedName(String filePath, String fileName, String data) {
	    Path path = Paths.get(filePath, fileName);
	    
	    try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile()))) {
	        // Ghi dữ liệu vào file
	        writer.write(data);

	        System.out.println("Dữ liệu đã được ghi vào file '" + fileName + "' thành công.");
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
}
