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

import utils.Const;
import utils.Format;

public class CrawlProvinceSource1 {

	private static final String[] AREA_LOTTERY = { Const.SOURCE_1 + Const.MIEN_TRUNG, Const.SOURCE_1 + Const.MIEN_NAM ,Const.SOURCE_1 + Const.MIEN_BAC };

	// lay tinh thanh pho co kqxs hom nay ( now )
	public static List<String> getProvinces() {
		ArrayList<String> result = new ArrayList<>();
		result.add(Const.NAME_NORTH);
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

	// danh sach tinh thanh pho
	public static Map<String, String> listProvinces() {
		Map<String, String> result = new HashMap<>();
		result.put(Format.extractProvinceName("Xổ số Hồ Chí Minh"), Const.MIEN_NAM+"/xo-so-ho-chi-minh");
		result.put(Format.extractProvinceName("Xổ số Đồng Tháp"), Const.MIEN_NAM+"/xo-so-dong-thap");
		result.put(Format.extractProvinceName("Xổ số Cà Mau"), Const.MIEN_NAM+"/xo-so-ca-mau");
		result.put(Format.extractProvinceName("Xổ số Bến Tre"), Const.MIEN_NAM+"/xo-so-ben-tre");
		result.put(Format.extractProvinceName("Xổ số Vũng Tàu"), Const.MIEN_NAM+"/xo-so-vung-tau");
		result.put(Format.extractProvinceName("Xổ số Bạc Liêu"), Const.MIEN_NAM+"/xo-so-bac-lieu");
		result.put(Format.extractProvinceName("Xổ số Đồng Nai"), Const.MIEN_NAM+"/xo-so-dong-nai");
		result.put(Format.extractProvinceName("Xổ số Cần Thơ"), Const.MIEN_NAM+"/xo-so-can-tho");
		result.put(Format.extractProvinceName("Xổ số Sóc Trăng"), Const.MIEN_NAM+"/xo-so-soc-trang");
		result.put(Format.extractProvinceName("Xổ số Tây Ninh"), Const.MIEN_NAM+"/xo-so-tay-ninh");
		result.put(Format.extractProvinceName("Xổ số An Giang"), Const.MIEN_NAM+"/xo-so-an-giang");
		result.put(Format.extractProvinceName("Xổ số Bình Thuận"), Const.MIEN_NAM+"/xo-so-binh-thuan");
		result.put(Format.extractProvinceName("Xổ số Vĩnh Long"), Const.MIEN_NAM+"/xo-so-vinh-long");
		result.put(Format.extractProvinceName("Xổ số Bình Dương"), Const.MIEN_NAM+"/xo-so-binh-duong");
		result.put(Format.extractProvinceName("Xổ số Trà Vinh"), Const.MIEN_NAM+"/xo-so-tra-vinh");
		result.put(Format.extractProvinceName("Xổ số Long An"), Const.MIEN_NAM+"/xo-so-long-an");
		result.put(Format.extractProvinceName("Xổ số Bình Phước"), Const.MIEN_NAM+"/xo-so-binh-phuoc");
		result.put(Format.extractProvinceName("Xổ số Hậu Giang"), Const.MIEN_NAM+"/xo-so-hau-giang");
		result.put(Format.extractProvinceName("Xổ số Tiền Giang"), Const.MIEN_NAM+"/xo-so-tien-giang");
		result.put(Format.extractProvinceName("Xổ số Kiên Giang"), Const.MIEN_NAM+"/xo-so-kien-giang");
		result.put(Format.extractProvinceName("Xổ số Đà Lạt"), Const.MIEN_NAM+"/xo-so-da-lat");
		result.put(Format.extractProvinceName("Xổ số Phú Yên"), Const.MIEN_TRUNG+"/xo-so-phu-yen");
		result.put(Format.extractProvinceName("Xổ số Thừa Thiên Huế"), Const.MIEN_TRUNG+"/xo-so-thua-thien-hue");
		result.put(Format.extractProvinceName("Xổ số Đắk Lắk"), Const.MIEN_TRUNG+"/xo-so-dac-lac");
		result.put(Format.extractProvinceName("Xổ số Quảng Nam"), Const.MIEN_TRUNG+"/xo-so-quang-nam");
		result.put(Format.extractProvinceName("Xổ số Đà Nẵng"), Const.MIEN_TRUNG+"/xo-so-da-nang");
		result.put(Format.extractProvinceName("Xổ số Khánh Hòa"), Const.MIEN_TRUNG+"/xo-so-khanh-hoa");
		result.put(Format.extractProvinceName("Xổ số Quảng Bình"), Const.MIEN_TRUNG+"/xo-so-quang-binh");
		result.put(Format.extractProvinceName("Xổ số Bình Định"), Const.MIEN_TRUNG+"/xo-so-binh-dinh");
		result.put(Format.extractProvinceName("Xổ số Quảng Trị"), Const.MIEN_TRUNG+"/xo-so-quang-tri");
		result.put(Format.extractProvinceName("Xổ số Gia Lai"), Const.MIEN_TRUNG+"/xo-so-gia-lai");
		result.put(Format.extractProvinceName("Xổ số Ninh Thuận"), Const.MIEN_TRUNG+"/xo-so-ninh-thuan");
		result.put(Format.extractProvinceName("Xổ số Quảng Ngãi"), Const.MIEN_TRUNG+"/xo-so-quang-ngai");
		result.put(Format.extractProvinceName("Xổ số Đắk Nông"), Const.MIEN_TRUNG+"/xo-so-dac-nong");
		result.put(Format.extractProvinceName("Xổ số Kon Tum"), Const.MIEN_TRUNG+"/xo-so-kon-tum");
		result.put(Format.extractProvinceName("Xổ số Miền Bắc"), Const.MIEN_BAC);

		return result;
	}
	
	// lay url
	public static String getUrls(String provice) {
		return listProvinces().containsKey(provice)?listProvinces().get(provice):"";
	}

	public static void main(String[] args) {
//		getProvinces().forEach(x -> System.out.println(x));
		System.out.println(getUrls(Const.NAME_NORTH));
	}
}
