package Presidente.TransactionApi;

import java.text.ParseException;

public class locationCheck extends Thread {

	DbFunctions db = new DbFunctions();
	Functions fun = new Functions();
	static String msg;
	static String sql = "select * from slot_clubs where not slot_club_id in (SELECT distinct slot_club_id FROM public.transactions WHERE transaction_time BETWEEN NOW() - INTERVAL '2 HOURS' AND NOW())";
	static String[] columns = { "slot_club_id" };

	public void run() {
		try {
			while (true) {
				if (fun.workTime()) {
					try {
						msg = db.executeQuery1(sql, "Sve lokacije salju podatke", columns);
						msg = fun.setUTF8(msg);
						fun.sendEmailYahho(msg, "resivojee@gmail.com", "Lokacije koje nisu slale podatke");
						fun.sendEmailYahho(msg, "pedjabg@gmail.com", "Lokacije koje nisu slale podatke");
						fun.sendEmailYahho(msg, "presidente.ks@gmail.com", "Lokacije koje nisu slale podatke");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				try {
					Thread.sleep(7200000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
