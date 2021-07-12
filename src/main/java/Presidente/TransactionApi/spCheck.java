package Presidente.TransactionApi;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class spCheck extends Thread {
	DbFunctions db = new DbFunctions();
	Functions fun = new Functions();
	static Connection lConn;
	static String url = "jdbc:postgresql://65.21.110.211:5432/accounting";
	static String user = "presidente";
	static String password = "test";
	static String spWithStatus11;
	static int reportIndex;
	static String spWorkStatus;

	public void run() {
		while (true) {
			try {
				lConn = DriverManager.getConnection(url, user, password); // Pokusava da otvori konekciju na bazu
				spWithStatus11 = db.executeFunction("SELECT public.get_json_sp_by_status(11)", lConn,
						"get_json_sp_by_status");
				// proverava da li u bazi ima izvestaja sa statusom 11
				reportIndex = fun.getReportIndex(spWithStatus11, "s"); // uzima reportindex za taj
				spWorkStatus = fun.getSpWorkStatus(reportIndex, db, lConn); // proveraba procedurom da li ima procesinga
																			// koji nerade kako treba
				if (spWithStatus11 != null && Integer.parseInt(spWorkStatus) == 1) {
					spProcessing badProcessing = spStart.nadjiProcessing(reportIndex);
					spStart.prekini(badProcessing);
				}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (SecurityException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
