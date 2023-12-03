package crawl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import utils.Const;
import utils.Format;

public class WriteToCSV {

	public static boolean write(List<String> tinhData, List<String> thuData, List<String> ngayXoSoData,
			List<String> giaiData, List<String> soTrungThuongData, List<String> khuVucData) {
		Workbook workbook = null;
		try {
			String dataFolderPath = Const.PATH;
			File dataFolder = new File(dataFolderPath);
			/*
			 * 16.Kiểm tra folder D:/data/ có tồn tại hay không?
			 */
			if (!dataFolder.exists()) {
				/*
				 * 16.1.Tạo folder D:/data/
				 */
				dataFolder.mkdirs();
			}
			File csvFile = new File(Format.generateFileName());

			/*
			 * 17.Kiểm tra file data.csv có tồn tại hay không?
			 */
			if (csvFile.exists()) {
				// Nếu tệp CSV đã tồn tại, mở nó
				FileInputStream fileInputStream = new FileInputStream(Format.generateFileName());
				workbook = new XSSFWorkbook(fileInputStream);
			} else {
				// Nếu tệp tin CSV không tồn tại, tạo một workbook mới
				workbook = new XSSFWorkbook();
			}

			// Lấy sheet hoặc tạo mới nếu không tồn tại
			Sheet sheet = workbook.getSheet(Const.NAME_SHEET);
			if (sheet == null) {
				sheet = workbook.createSheet(Const.NAME_SHEET);
			}

			int lastRowNum = sheet.getLastRowNum();
			// Ghi dữ liệu từ các danh sách vào các cột tương ứng trong sheet
			for (int i = 0; i < tinhData.size(); i++) {
				Row dataRow = sheet.createRow(lastRowNum + 1 + i);
				dataRow.createCell(0).setCellValue(tinhData.get(i));
				dataRow.createCell(1).setCellValue(thuData.get(i));
				dataRow.createCell(2).setCellValue(ngayXoSoData.get(i));
				dataRow.createCell(3).setCellValue(giaiData.get(i));
				dataRow.createCell(4).setCellValue(soTrungThuongData.get(i));
				dataRow.createCell(5).setCellValue(khuVucData.get(i));
			}
			/*
			 * 17.1.Tạo file data.csv
			 * */

			try (FileOutputStream outputStream = new FileOutputStream(Format.generateFileName())) {
				/*
				 * 18.Ghi dữ liệu vào tập tin
				 * 
				 * 19 bên CrawlDataSource1 line 182
				 * */
				 workbook.write(outputStream);
				System.out.println("Data has been written to " + Format.generateFileName() + " successfully.");
				return true;
			}catch (Exception e) {
				
				return false;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}


}