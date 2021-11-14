package Presidente.TransactionApi;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class locationCheck extends Thread {

	DbFunctions db = new DbFunctions();
	Functions fun = new Functions();
	static Connection lConn;
	static String msg;
	static String sql = "select * from slot_clubs where not slot_club_id in (SELECT distinct slot_club_id FROM public.transactions WHERE transaction_time BETWEEN NOW() - INTERVAL '2 HOURS' AND NOW())";
	static String[] columns = {"slot_club_id", "adresa", "opstina", "mesto", "slot_club_sid"};
	
	public void run() {
		while (true) {
			try {
				try {
					try {
						msg = db.executeQuery1(sql, "Sve lokacije salju podatke", columns);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//fun.sendEmail(msg);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				Thread.sleep(7200000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
