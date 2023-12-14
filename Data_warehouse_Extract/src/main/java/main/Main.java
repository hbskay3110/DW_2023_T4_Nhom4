package main;

import crawl.CrawlDataSource1;
import utils.Const;

public class Main {
	public static void main(String[] args) {
		int id_source = args.length > 0 ? Integer.parseInt(args[0]) : 1;
		String date_lottery = args.length > 1 ? args[1] : "";
		// xét id cho nguồn cần chạy
		Const.setIdSource_1(id_source);
		// xét ngày cần chạy ( nếu có)
		Const.setDate(date_lottery);
		CrawlDataSource1 s1 = new CrawlDataSource1();
		s1.execSource1();
		
		// xét khoảng ngày cần crawl
//		for (int i = 13; i <= 13; i++) {
//			Const.setIdSource_1(1);
//			// xét ngày cần chạy ( nếu có)
//			Const.setDate(i + "-12-2023");
//			CrawlDataSource1 s = new CrawlDataSource1();
//			s.execSource1();
//		}
	}
}
