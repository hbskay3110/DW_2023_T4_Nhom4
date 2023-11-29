package crawl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import utils.Const;
import utils.Format;

public class WriteToCSV {

	public static boolean write( List<String> tinhData, List<String> thuData,
			List<String> ngayXoSoData, List<String> giaiData, List<String> soTrungThuongData
			,List<String> khuVucData) {
		Workbook workbook = null;
		try {
			String dataFolderPath = Const.PATH;
            File dataFolder = new File(dataFolderPath);
			if (!dataFolder.exists()) {
                // Nếu thư mục không tồn tại, tạo thư mục mới
                dataFolder.mkdirs();
            }
			// Kiểm tra xem tệp CSV đã tồn tại chưa
			File csvFile = new File(Format.generateFileName());

			if (csvFile.exists()) {
				// Nếu tệp CSV đã tồn tại, mở nó
				FileInputStream fileInputStream = new FileInputStream(Format.generateFileName());
				workbook = new XSSFWorkbook(fileInputStream);
			} else {
				// Nếu tệp CSV chưa tồn tại, tạo một tệp mới
				workbook = new XSSFWorkbook();
			}

			  // Lấy sheet hoặc tạo mới nếu không tồn tại
            Sheet sheet = workbook.getSheet(Const.NAME_SHEET);
            if (sheet == null) {
                sheet = workbook.createSheet(Const.NAME_SHEET);
            }

            int lastRowNum = sheet.getLastRowNum();
            // ghi vào file
			for (int i = 0; i < tinhData.size(); i++) {
				Row dataRow = sheet.createRow(lastRowNum + 1 + i);
				dataRow.createCell(0).setCellValue(tinhData.get(i));
				dataRow.createCell(1).setCellValue(thuData.get(i));
				dataRow.createCell(2).setCellValue(ngayXoSoData.get(i));
				dataRow.createCell(3).setCellValue(giaiData.get(i));
				dataRow.createCell(4).setCellValue(soTrungThuongData.get(i));
				dataRow.createCell(5).setCellValue(khuVucData.get(i));
			}

			// ghi
			try (FileOutputStream outputStream = new FileOutputStream(Format.generateFileName())) {
				workbook.write(outputStream);
				System.out.println("Data has been written to " + Format.generateFileName() + " successfully.");
				return true;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}