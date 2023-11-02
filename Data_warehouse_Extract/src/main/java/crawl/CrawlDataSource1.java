package crawl;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import io.FileManage;
import utils.Const;
import utils.Format;

public class CrawlDataSource1 {

	@SafeVarargs
	public static void crawDataProvinceSource1(String url, List<String>... list) {
		if (!checkLenghtListParam(list)) {
			System.out.println("Not enough list data");
			return;
		}
		Response response = null;
		try {
			response = ConnectionSource1.connectLink(Const.SOURCE_1.trim() + url);
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
									dataByPrize.put(prize, existingData + ", " + dataValue);
								} else {
									dataByPrize.put(prize, dataValue);
								}
							}
						}
					}
					// nap vao danh sach
					for (Map.Entry<String, String> entry : dataByPrize.entrySet()) {

						String prize = entry.getKey();
						String dataValues = entry.getValue();
						list[0].add(document.select("button.btn-select-lottery").text().trim());
						list[1].add(Format.formatWeekdaysFromText(table.select("h2").text().trim()));
						list[2].add(Format.formatDateFromText(table.select("h2").text().trim()));
						list[3].add(prize);
						list[4].add(dataValues);
						String khuVuc = Format.generateArea(url);
						list[5].add(khuVuc);

					}
					// ghi vào file
					WriteToCSV.write(list[0], list[1], list[2], list[3], list[4], list[5]);
				} else {
					System.out.println("Table not found on the page.");
				}
			} else {
				System.out.println("Request failed with status code: " + response.statusCode());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public static boolean checkLenghtListParam(List<String>... lists) {
		if (lists.length == 6)
			return true;
		return false;

	}
	// thuc thi source 1
	public void execSource1() {
		ArrayList<String> listProvinceTodays = (ArrayList<String>) CrawlProvinceSource1.getProvinces();
		for (int i = 0; i < listProvinceTodays.size(); i++) {
			// Sample data (replace with actual data)
			List<String> tinhData = new ArrayList<String>();
			List<String> thuData = new ArrayList<String>();
			List<String> ngayXoSoData = new ArrayList<String>();
			List<String> giaiData = new ArrayList<String>();
			List<String> soTrungThuongData = new ArrayList<String>();
			List<String> khuVucData = new ArrayList<String>();
			String urlProvince = CrawlProvinceSource1.getUrls(listProvinceTodays.get(i));
			crawDataProvinceSource1(urlProvince, tinhData, thuData, ngayXoSoData, giaiData, soTrungThuongData,
					khuVucData);

		}
	}



}
