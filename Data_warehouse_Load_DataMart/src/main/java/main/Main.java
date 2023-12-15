package main;

import java.util.List;
import java.util.Properties;

import org.jdbi.v3.core.Jdbi;

import dao.ControlService;
import dao.DataConfig;
import dao.DataMartService;
import dao.DataWarehouseService;
import dao.DatabaseManager;
import dao.EDatabase;
import utils.Const;
import utils.EStatus;

public class Main {
private static Jdbi dataMartJdbi ;
private static Jdbi dataWarehouseJdbi ;
	ControlService controlService;
	DataWarehouseService dataWarehouseService;
	DataMartService dataMartService;

	public Main() {
		controlService = new ControlService();
		
	}

	// 1 - 5
	public void executeLoadDataMart() {
		// 1 load config
		Properties properties = DatabaseManager.loadProperties(EDatabase.CONTROL.toString().toLowerCase());
//2 kết nối control
		Jdbi connectConfigDB = DatabaseManager.createJdbi(properties);
		// 3
		if (connectConfigDB == null) {
			return;
		}
		// 4 lấy 1 dòng
		DataConfig dataConfig = ControlService.getDataConfig(Const.idSource_1);
		// 5 set  
		DatabaseManager.connectDatamartJdbi(dataConfig.getServerName(), dataConfig.getPort(),
				dataConfig.getDatabaseNameMart(), dataConfig.getUser(), dataConfig.getPass());
		DatabaseManager.connectDatawarehouse(dataConfig.getServerName(), dataConfig.getPort(),
				dataConfig.getDatabaseNameDatawarehouse(), dataConfig.getUser(), dataConfig.getPass());
		// 6 ket
		dataMartJdbi = DatabaseManager.getDatamartJdbi();
		dataWarehouseJdbi = DatabaseManager.getDatawarehouseJdbi();
		String tableTemp = "";
		/*
		 * 7.Kiểm tra module Aggregate ngày hôm này đã chạy xong chưa?: lấy 1 dòng trong
		 * bảng data_files với id = id , ngày = now() và status = "CLA" với limit =1
		 */

		boolean checkAggregateExecuteToday = EStatus.CLA.name()
				.equals(controlService.getStatusAggregateToday(Const.idSource_1));
		if (checkAggregateExecuteToday) {
			return;
		}
		/*
		 * 8.Kiểm tra xem hôm nay module này đã chạy chưa?: lấy 1 dòng trong bảng
		 * data_files với id = id , ngày = now() và status = "CLM" với limit =1
		 */

		boolean checkLoadDataMartExecuteToday = EStatus.CLM.name()
				.equals(ControlService.getStatusLoadDataMartToday(Const.idSource_1));

		if (checkLoadDataMartExecuteToday) {
			System.out.println("Hôm nay ban đã chạy rồi!");
			return;
		}

		/*
		 * 9.Chèn 1 dòng vào bảng data_files với status = "BLM"
		 */
		ControlService.addStatus(Const.idSource_1, EStatus.BLM.getNote(), EStatus.BLM.name());

		/*
		 * 10. Lấy ra các bảng có tên là aggregates trong datawarehouse
		 */

		List<String> listTableAggreates = DataWarehouseService.getAggregateTables();

		if (listTableAggreates.isEmpty()) {
			return;
		}
		for (String agg : listTableAggreates) {

			/*
			 * 11.Kiểm tra xem đã có các bảng này trên datamart chưa?
			 */
			if (!DataMartService.tableExists(agg, dataMartJdbi.open())) {
				// 12. Tạo bảng

				boolean isCreateSuccess = dataMartService.copyTable(agg);
				// 13. Tạo bảng thành công hay không?
				if (!isCreateSuccess) {
					// 13.1 .Chèn 1 dòng vào bảng data_files với status = "FLM"
					ControlService.addStatus(Const.idSource_1, EStatus.FLM.getNote(), EStatus.FLM.name());
					return;
				}

			} else {
				// 11.1 ở DataMartService
				// 11.2. Tạo bảng tạm
				tableTemp = DataMartService.createTableTempFromMainTable(agg);

				if (tableTemp != null) {
					System.out.println("Đã tạo bảng " + tableTemp);
					// 11.3. Copy ra bảng tạm
					dataMartService.copyToDataTableInMart(agg, tableTemp);
					System.out.println("Đã copy data vào bảng " + tableTemp);
				} else {
					// 11.4. Truncate bảng tạm
					tableTemp = agg + "_temps";
					DataMartService.truncateTable(tableTemp);
				}

				// 11.5. Truncate dữ liệu bảng aggrate trên các bảng datamart
				DataMartService.truncateTable(agg);
				System.out.println("Đã truncate bảng: " + agg);
			}

			// 14. Copy dữ liệu từ datawarehouse vào datamart
			boolean isCopyDataSuccess = dataMartService.copyDataFromDatawarehouseToDatamart(agg);
			// 15. Thành công hay không?
			if (!isCopyDataSuccess) {
				// 15.1. Copy từ bảng tạm vào lại bảng aggregate
				dataMartService.copyToDataTableInMart(tableTemp, agg);
				// 15.2. Truncate bảng tạm
				dataMartService.truncateTable(tableTemp);
				// 15.3.Chèn 1 dòng vào bảng data_files với status = "FLM"
				this.controlService.addStatus(Const.idSource_1, EStatus.FLM.getNote(), EStatus.FLM.name());
				return;
			}

		}
		// 15.4.Chèn 1 dòng vào bảng data_files với status = "CLM"
		System.out.println("Ok");
		this.controlService.addStatus(Const.idSource_1, EStatus.CLM.getNote(), EStatus.CLM.name());
		return;

	}

	public static void main(String[] args) {
		int id_source = args.length > 0 ? Integer.parseInt(args[0]) : 1;
		// xét id cho nguồn cần chạy
		Const.setIdSource_1(id_source);
		Main main = new Main();
		main.executeLoadDataMart();
	}

}
