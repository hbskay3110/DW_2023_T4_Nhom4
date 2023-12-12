package main;

import java.util.List;

import org.jdbi.v3.core.Jdbi;

import dao.ControlService;
import dao.DataMartService;
import dao.DataWarehouseService;
import dao.DatabaseManager;
import utils.Const;
import utils.EStatus;

public class Main {
	private final Jdbi dataMartJdbi = DatabaseManager.getDatamartJdbi();
	ControlService controlService;
	DataWarehouseService dataWarehouseService;
	DataMartService dataMartService;

	public Main() {
		controlService = new ControlService();
		dataWarehouseService = new DataWarehouseService();
		dataMartService = new DataMartService();
	}

	public void executeLoadDataMart() {
		String tableTemp = "";
		// 6.
		boolean checkAggregateExecuteToday = EStatus.CLA.name()
				.equals(controlService.getStatusAggregateToday(Const.idSource_1));
		if (checkAggregateExecuteToday) {
			return;
		}
		// 7.
		boolean checkLoadDataMartExecuteToday = EStatus.CLM.name()
				.equals(controlService.getStatusLoadDataMartToday(Const.idSource_1));

		if (checkLoadDataMartExecuteToday) {
			System.out.println("Hôm nay ban đã chạy rồi!");
			return;
		}

		// 8.
		this.controlService.addStatus(Const.idSource_1, EStatus.BLM.getNote(), EStatus.BLM.name());

		// 9

		List<String> listTableAggreates = dataWarehouseService.getAggregateTables();

		if (listTableAggreates.isEmpty()) {
			return;
		}
		for (String agg : listTableAggreates) {

			// 10.
			if (!dataMartService.tableExists(agg, dataMartJdbi.open())) {
				// 11.

				boolean isCreateSuccess = dataMartService.copyTable(agg);
				// 12.
				if (!isCreateSuccess) {
					// 12.1
					this.controlService.addStatus(Const.idSource_1, EStatus.FLM.getNote(), EStatus.FLM.name());
					return;
				}

			} else {
				// 10.1
				tableTemp = dataMartService.createTableTempFromMainTable(agg);
				
				if (tableTemp != null) {
					System.out.println("Đã tạo bảng " + tableTemp);
					dataMartService.copyToDataTableInMart(agg, tableTemp);
					System.out.println("Đã copy data vào bảng " + tableTemp);
				}
				else {
					tableTemp = agg+"_temps";
					dataMartService.truncateTable(tableTemp);
				}
				

				// 10.2
				dataMartService.truncateTable(agg);
				System.out.println("Đã truncate bảng " + agg);
			}

			// 13.
			boolean isCopyDataSuccess = dataMartService.copyDataFromDatawarehouseToDatamart(agg);
			// 14.
			if (!isCopyDataSuccess) {
				// 14.2
				dataMartService.copyToDataTableInMart(tableTemp, agg);
				// 14.3
				dataMartService.truncateTable(tableTemp);
				this.controlService.addStatus(Const.idSource_1, EStatus.FLM.getNote(), EStatus.FLM.name());
				return;
			}

		}
		// 14.1
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
