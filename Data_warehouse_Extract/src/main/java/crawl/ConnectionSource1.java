package crawl;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.jsoup.Jsoup;
import org.jsoup.Connection.Response;


public class ConnectionSource1 {
	
	public static Response connectLink(String link) {
		Response response = null;
		try {
			TrustManager[] trustAllCertificates = new TrustManager[] { new X509TrustManager() {
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
				public void checkClientTrusted(X509Certificate[] certs, String authType) {
				}

				public void checkServerTrusted(X509Certificate[] certs, String authType) {
				}
			} };

			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, trustAllCertificates, new java.security.SecureRandom());

			response = Jsoup.connect(link)
					.sslSocketFactory(sslContext.getSocketFactory()).ignoreHttpErrors(true)
					.ignoreContentType(true).execute();
		} catch (KeyManagementException | NoSuchAlgorithmException | IOException e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
		}
		return response;
	}
	
	public static void main(String[] args) {
	}

}
