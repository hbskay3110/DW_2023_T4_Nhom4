package crawl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import model.ERegion;
import utils.Const;
import utils.Format;

public class CrawlProvinceSource1 {

	/*
	 * paramDate: lưu ngày nếu người dùng truyền param khi chạy thủ công
	 * 
	 * */
	 String paramDate = Const.date.length() == 0 ? "" : "?date=" + Const.date;
	private final String[] AREA_LOTTERY = { Const.SOURCE_1 + ERegion.MIEN_TRUNG.getCode() + paramDate,
			Const.SOURCE_1 + ERegion.MIEN_NAM.getCode() + paramDate, Const.SOURCE_1 + ERegion.MIEN_BAC.getCode() + paramDate };

	/*
	 * hàm getProvinces()
	 * Chức năng: lấy các đài được công bố kết quả hiện tại
	 * */
	public  List<String> getProvinces() {
		ArrayList<String> result = new ArrayList<>();
		result.add(ERegion.MIEN_BAC.getDescription());
		Response response = null;
		for (int i = 0; i < AREA_LOTTERY.length; i++) {

			try {
				response = crawl.ConnectionSource1.connectLink(AREA_LOTTERY[i].trim());

				if (response.statusCode() == 200) {
					Document document = response.parse();
					// Find the table with the specified class
					Element table = document.select("table.table-result-lottery").first();
					// Extract the provinces from the table
					if (table != null) {
						Elements provinceElements = table.select("td.results span.wrap-text");
						for (Element provinceElement : provinceElements) {
							String province = provinceElement.text();
							result.add(province);
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/*
	 * hàm listProvinces()
	 * Chức năng: lưu tất cả các đài xổ số ở Việt Nam
	 * */
	public static Map<String, String> listProvinces() {
		Map<String, String> result = new HashMap<>();
		result.put(Format.extractProvinceName("Xổ số Hồ Chí Minh"), ERegion.MIEN_NAM.getCode() + "/xo-so-ho-chi-minh");
		result.put(Format.extractProvinceName("Xổ số Đồng Tháp"), ERegion.MIEN_NAM.getCode() + "/xo-so-dong-thap");
		result.put(Format.extractProvinceName("Xổ số Cà Mau"), ERegion.MIEN_NAM.getCode() + "/xo-so-ca-mau");
		result.put(Format.extractProvinceName("Xổ số Bến Tre"), ERegion.MIEN_NAM.getCode() + "/xo-so-ben-tre");
		result.put(Format.extractProvinceName("Xổ số Vũng Tàu"), ERegion.MIEN_NAM.getCode() + "/xo-so-vung-tau");
		result.put(Format.extractProvinceName("Xổ số Bạc Liêu"), ERegion.MIEN_NAM.getCode() + "/xo-so-bac-lieu");
		result.put(Format.extractProvinceName("Xổ số Đồng Nai"), ERegion.MIEN_NAM.getCode() + "/xo-so-dong-nai");
		result.put(Format.extractProvinceName("Xổ số Cần Thơ"), ERegion.MIEN_NAM.getCode() + "/xo-so-can-tho");
		result.put(Format.extractProvinceName("Xổ số Sóc Trăng"), ERegion.MIEN_NAM.getCode() + "/xo-so-soc-trang");
		result.put(Format.extractProvinceName("Xổ số Tây Ninh"), ERegion.MIEN_NAM.getCode() + "/xo-so-tay-ninh");
		result.put(Format.extractProvinceName("Xổ số An Giang"), ERegion.MIEN_NAM.getCode() + "/xo-so-an-giang");
		result.put(Format.extractProvinceName("Xổ số Bình Thuận"), ERegion.MIEN_NAM.getCode() + "/xo-so-binh-thuan");
		result.put(Format.extractProvinceName("Xổ số Vĩnh Long"), ERegion.MIEN_NAM.getCode() + "/xo-so-vinh-long");
		result.put(Format.extractProvinceName("Xổ số Bình Dương"), ERegion.MIEN_NAM.getCode() + "/xo-so-binh-duong");
		result.put(Format.extractProvinceName("Xổ số Trà Vinh"), ERegion.MIEN_NAM.getCode() + "/xo-so-tra-vinh");
		result.put(Format.extractProvinceName("Xổ số Long An"), ERegion.MIEN_NAM.getCode() + "/xo-so-long-an");
		result.put(Format.extractProvinceName("Xổ số Bình Phước"), ERegion.MIEN_NAM.getCode() + "/xo-so-binh-phuoc");
		result.put(Format.extractProvinceName("Xổ số Hậu Giang"), ERegion.MIEN_NAM.getCode() + "/xo-so-hau-giang");
		result.put(Format.extractProvinceName("Xổ số Tiền Giang"), ERegion.MIEN_NAM.getCode() + "/xo-so-tien-giang");
		result.put(Format.extractProvinceName("Xổ số Kiên Giang"), ERegion.MIEN_NAM.getCode() + "/xo-so-kien-giang");
		result.put(Format.extractProvinceName("Xổ số Đà Lạt"), ERegion.MIEN_NAM.getCode() + "/xo-so-da-lat");
		result.put(Format.extractProvinceName("Xổ số Phú Yên"), ERegion.MIEN_TRUNG.getCode() + "/xo-so-phu-yen");
		result.put(Format.extractProvinceName("Xổ số Thừa Thiên Huế"), ERegion.MIEN_TRUNG.getCode() + "/xo-so-thua-thien-hue");
		result.put(Format.extractProvinceName("Xổ số Đắk Lắk"), ERegion.MIEN_TRUNG.getCode() + "/xo-so-dac-lac");
		result.put(Format.extractProvinceName("Xổ số Quảng Nam"), ERegion.MIEN_TRUNG.getCode() + "/xo-so-quang-nam");
		result.put(Format.extractProvinceName("Xổ số Đà Nẵng"), ERegion.MIEN_TRUNG.getCode() + "/xo-so-da-nang");
		result.put(Format.extractProvinceName("Xổ số Khánh Hòa"), ERegion.MIEN_TRUNG.getCode() + "/xo-so-khanh-hoa");
		result.put(Format.extractProvinceName("Xổ số Quảng Bình"), ERegion.MIEN_TRUNG.getCode() + "/xo-so-quang-binh");
		result.put(Format.extractProvinceName("Xổ số Bình Định"), ERegion.MIEN_TRUNG.getCode() + "/xo-so-binh-dinh");
		result.put(Format.extractProvinceName("Xổ số Quảng Trị"), ERegion.MIEN_TRUNG.getCode() + "/xo-so-quang-tri");
		result.put(Format.extractProvinceName("Xổ số Gia Lai"), ERegion.MIEN_TRUNG.getCode() + "/xo-so-gia-lai");
		result.put(Format.extractProvinceName("Xổ số Ninh Thuận"), ERegion.MIEN_TRUNG.getCode() + "/xo-so-ninh-thuan");
		result.put(Format.extractProvinceName("Xổ số Quảng Ngãi"), ERegion.MIEN_TRUNG.getCode() + "/xo-so-quang-ngai");
		result.put(Format.extractProvinceName("Xổ số Đắk Nông"), ERegion.MIEN_TRUNG.getCode() + "/xo-so-dac-nong");
		result.put(Format.extractProvinceName("Xổ số Kon Tum"), ERegion.MIEN_TRUNG.getCode() + "/xo-so-kon-tum");
		result.put(Format.extractProvinceName("Xổ số Miền Bắc"), ERegion.MIEN_BAC.getCode());

		return result;
	}

	/*
	 * hàm getUrls(String provice)
	 * Nhận vào tên đài xổ số
	 * Trả về: Đường dẫn URL ứng với tên tỉnh
	 * */
	public static String getUrls(String provice) {
		return listProvinces().containsKey(provice) ? listProvinces().get(provice) : "";
	}


}
