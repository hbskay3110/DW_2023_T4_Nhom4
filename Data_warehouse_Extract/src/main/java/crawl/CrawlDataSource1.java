package crawl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import db.DaoControl;
import model.ENameFieldCollection;
import model.EStatus;
import utils.Const;
import utils.Format;

public class CrawlDataSource1 {

	DaoControl control;
	String dateParam;

	public CrawlDataSource1() {
		control = new DaoControl();
		dateParam = new CrawlProvinceSource1().paramDate;
	}

	/*
	 * hàm crawDataProvinceSource1(String url, List<String>... list) nhận vào tham
	 * số url trang web cần crawl dữ liệu , truyền vào các collection List<String>
	 * để lưu dữ liệu khi hoàn thành
	 */
	public Map<String, List<String>> crawDataProvinceSource1(String url, List<String>... list) {
		Response response = null;
		if (!checkLenghtListParam(list)) {
			System.out.println("Not enough list data");
			return null;
		}
		try {
			response = ConnectionSource1.connectLink(Const.SOURCE_1.trim() + url + dateParam);
			// Check if the request was successful
			if (response.statusCode() == 200) {
				Document document = response.parse();

				// Find the table with the specified class
				Element table = document.select("table.table-result-lottery").first();

				// Initialize a Map to organize data by data-prize value
				Map<String, String> dataByPrize = new HashMap<>();

				// Check if the table is found
				if (table != null) {
					// Iterate through rows in the table
					for (Element row : table.select("tr")) {
						Elements spanElements = row.select("span.number");
						Elements cells = row.select("td.prize");
						if (!cells.isEmpty()) {
							// Iterate through the <span> elements
							for (Element spanElement : spanElements) {
								String dataValue = spanElement.attr("data-value");
								String prize = cells.get(0).text().trim();

								// Check if dataPrize already exists in the map
								if (dataByPrize.containsKey(prize)) {
									String existingData = dataByPrize.get(prize);
									dataByPrize.put(prize, existingData + " " + dataValue);
								} else {
									dataByPrize.put(prize, dataValue);
								}
							}
						}
					}
					// nap vao danh sach
					for (Map.Entry<String, String> entry : dataByPrize.entrySet()) {
						String locations = document.select("button.btn-select-lottery").text().trim();
						String regions = Format.generateArea(url);
						String weekdays = Format.formatWeekdaysFromText(table.select("h2").text().trim());
						String prizes = entry.getKey();
						String date = Format.formatDateFromText(table.select("h2").text().trim());
						String results = entry.getValue();
						list[0].add(locations);
						list[1].add(weekdays);
						list[2].add(date);
						list[3].add(prizes);
						list[4].add(results);
						list[5].add(regions);
					}
					Map<String, List<String>> data = new TreeMap<>();
					data.put(ENameFieldCollection.LOCATION.getNameColumn(),
							list[ENameFieldCollection.LOCATION.getCol()]);
					data.put(ENameFieldCollection.WEEKDAYS.getNameColumn(),
							list[ENameFieldCollection.WEEKDAYS.getCol()]);
					data.put(ENameFieldCollection.DATE.getNameColumn(), list[ENameFieldCollection.DATE.getCol()]);
					data.put(ENameFieldCollection.PRIZES.getNameColumn(), list[ENameFieldCollection.PRIZES.getCol()]);
					data.put(ENameFieldCollection.RESULTS.getNameColumn(), list[ENameFieldCollection.RESULTS.getCol()]);
					data.put(ENameFieldCollection.REGIONS.getNameColumn(), list[ENameFieldCollection.REGIONS.getCol()]);
					return data;
				} else {
					System.out.println("Table not found on the page.");
					this.control.addStatus(Const.idSource_1, "Table not found on the page.", EStatus.FE.name());

				}
			} else {
				System.out.println("Request failed with status code: " + response.statusCode());
				this.control.addStatus(Const.idSource_1, "Request failed with status code: " + response.statusCode(),
						EStatus.FE.name());
			}
		} catch (IOException e) {
			this.control.addStatus(Const.idSource_1, e.getMessage(), EStatus.FE.name());
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * hàm checkLenghtListParam(List<String>... lists) Kiểm tra xem có phải có 6
	 * collection List<String> được truyền vào hay không bởi vì có 6 trường thuộc
	 * tính: Địa điểm, Thứ, Ngày ,Giải ,Kết quả, Vùng
	 */
	@SuppressWarnings("unchecked")
	public static boolean checkLenghtListParam(List<String>... lists) {
		if (lists.length == Const.QUANTITY_ATTRIBUTE)
			return true;
		return false;

	}

	/*
	 * hàm isExtractSuccess(ArrayList<String>listProvinceTodays) kiểm tra xem việc
	 * Crawl kèm với ghi có thành công hay không
	 */
	public boolean isExtractAndWriteCSV(ArrayList<String> listProvinceTodays) {
		boolean result = true;
		/*
		 * 10.Lấy ra 1 tỉnh trong danh sách
		 */
		Iterator<String> iterator = listProvinceTodays.iterator();
		while (iterator.hasNext()) {
			/*
			 * 11.Lấy ra 1 tỉnh trong danh sách
			 */
			String province = iterator.next();
			List<String> locations = new ArrayList<>();
			List<String> weekdays = new ArrayList<>();
			List<String> date = new ArrayList<>();
			List<String> prize = new ArrayList<>();
			List<String> results = new ArrayList<>();
			List<String> regions = new ArrayList<>();

			/*
			 * 12.Lấy url tỉnh đó
			 */
			String url = CrawlProvinceSource1.getUrls(province);

			/*
			 * 13.Crawl dữ liệu từ url
			 */
			Map<String, List<String>> data = crawDataProvinceSource1(url, locations, weekdays, date, prize, results,
					regions);
			/*
			 * 14.Kiểm tra có crawl thành công hay không?
			 */
			boolean isCrawlSuccess = !data.isEmpty();
			if (!isCrawlSuccess) {
				/*
				 * 14.1 Chèn 1 dòng vào bảng data_files với status = "FE" nếu không thành công
				 */
				this.control.addStatus(Const.idSource_1, "Fail Extract", EStatus.FE.name());
				result = false;
				break;
			}

			/*
			 * 15.Ghi vào file data.csv trong D:/data/data.csv 16->18 bên WriteToCSV.write
			 */

			boolean isWriteSuccess = WriteToCSV.write(data.get("locations"), data.get("weekdays"), data.get("date"),
					data.get("prizes"), data.get("results"), data.get("regions"));
			/*
			 * 19.Kiểm tra có ghi thành công hay không?
			 */
			if (isWriteSuccess) {
				result = true;
			} else {
				/*
				 * 19.1 Xóa file data.csv
				 */
				File file = new File(Format.generateFileName());
				file.delete();
				/*
				 * 19.2 Chèn 1 dòng vào bảng data_files với status = "FE"
				 */
				this.control.addStatus(Const.idSource_1, "Fail Extract", EStatus.FE.name());
				// end
				result = false;
				break;
			}

		}
		return result;
	}

	/*
	 * hàm execSource1() Hàm thực thi tất cả các bước
	 */
	public void execSource1() {
		/*
		 * 1->5.Load các cấu hình database từ file database.properties
		 */
		Const.loadConfFromDB();
		/*
		 * 6.Kiểm tra xem ngày hôm này đã chạy chưa? bằng cách lấy 1 dòng trong bảng
		 * data_files với điều kiện id bằng id nhận từ arguments , thời gian là ngày hôm
		 * nay và status = "CE"
		 * 
		 * Nếu có nhập ngày thì kiểm tra xem ngày đó đã chạy hoàn thành chưa
		 */
		
		boolean isExtractComplete = Const.date.isEmpty()
				? EStatus.CE.name().equals(this.control.getStatusToday(Const.idSource_1)) ? true : false
				: EStatus.CE.name().equals(this.control.getStatusByDate(Const.idSource_1, Const.date)) ? true : false;
		if (!isExtractComplete) {
			/*
			 * 7. Chèn 1 dòng vào bảng data_files với status = "BE"
			 */
			this.control.addStatus(Const.idSource_1, "Begin Extract", EStatus.BE.name());
			/*
			 * 8. Crawl dữ liệu từ trang web lấy ra danh sách tỉnh ngày hôm nay xổ số
			 */
			ArrayList<String> listProvinceTodays = (ArrayList<String>) new CrawlProvinceSource1().getProvinces();

			/*
			 * 9. Kiểm tra danh sách tỉnh có trống hay không?
			 */
			if (listProvinceTodays.isEmpty()) {
				/*
				 * 9.1 Chèn 1 dòng vào bảng data_files với status = "FE"
				 */
				this.control.addStatus(Const.idSource_1, "Fail Extract", EStatus.FE.name());
				return;
			}
			/*
			 * 10 -> 19
			 */
			boolean isSuccess = isExtractAndWriteCSV(listProvinceTodays);
			/*
			 * 20.Kiểm tra quá trình extract và ghi vào file có thành công hay không?
			 */
			if (isSuccess) {
				/*
				 * 20.1.Chèn 1 dòng vào bảng data_files với status = "CE"
				 */
				this.control.addStatus(Const.idSource_1, "Complete Extract", EStatus.CE.name());
			} else {
				/*
				 * 20.2 Chèn 1 dòng vào bảng data_files với status = "FE"
				 */
				this.control.addStatus(Const.idSource_1, "Fail Extract", EStatus.FE.name());
				return;
			}

		} else {
			/*
			 * Kết thúc bước 6
			 */
			System.out.println(Const.date == null ? "Hôm nay bạn đã crawl rồi!"
					: "Bạn đã crawl dữ liệu ngày " + Const.date + " rồi!");
			return;
		}
	}

}
