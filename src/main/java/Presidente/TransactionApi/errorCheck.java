package Presidente.TransactionApi;

import java.io.IOException;
import java.sql.Connection;

public class ErrorCheck extends Thread {

	DbFunctions db = new DbFunctions();
	Functions fun = new Functions();
	static String msg;
	static String sql = "select * from slot_clubs where not slot_club_id in (SELECT distinct slot_club_id FROM public.transactions WHERE transaction_time BETWEEN NOW() - INTERVAL '2 HOURS' AND NOW())";
	static String[] columns = {"slot_club_id", "adresa", "opstina", "mesto", "slot_club_sid"};
	
	@Override
	public void run() {
		while(true) {
			fun.checkIsLogExist("logs");
			try {
				msg = db.executeQuery1(sql, "Sve lokacije salju podatke", columns);
			} catch (SecurityException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			fun.sendEmail(msg);
			try {
				Thread.sleep(7200000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
