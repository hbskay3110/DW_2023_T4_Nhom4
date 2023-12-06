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
	}
}
