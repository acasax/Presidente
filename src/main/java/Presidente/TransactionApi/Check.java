package Presidente.TransactionApi;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.json.JSONObject;

public class Check extends Thread {
	DbFunctions db = new DbFunctions();
	Functions fun = new Functions();
	static Connection lConn;
	
	static String transactionWithStatus11;
	static String transactionId;
	static String transactionWorkStatus;

	@Override
	public void run() {
		while (true) {

			try {
				// proverava da li u bazi ima transakcija sa statusom 11
				transactionWithStatus11 = db.executeFunction("SELECT public.get_json_by_status(11)", "get_json_by_status");
				//kreira parametre
				if(transactionWithStatus11 != null) {
					transactionId = fun.getTransansactionId(transactionWithStatus11, "s"); // uzima transaction_id za taj
					transactionWorkStatus = fun.getWorkStatus(transactionId, db);
				}
				//ubija los proces
				if (transactionWithStatus11 != null && Integer.parseInt(transactionWorkStatus) == 1) {
					Processing badProcessing = App.nadjiProcessing(transactionId);
					App.prekini(badProcessing);
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
