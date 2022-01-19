package Presidente.TransactionApi;

import java.text.ParseException;

public class paymentCheck extends Thread {

	DbFunctions db = new DbFunctions();
	Functions fun = new Functions();
	static String msg;
	static String sql = "SELECT SUM(transaction_amount) as ukupno, slot_club_id, transaction_types FROM public.transactions WHERE transaction_time BETWEEN CURRENT_DATE - INTEGER '1' + TIME '06:45:59' AND CURRENT_DATE + TIME '03:59:59' GROUP BY slot_club_id,  transaction_types";
	static String[] columns = { "ukupno", "slot_club_id", "transaction_types" };

	public void run() {
		while(true) {
			try {
				if(!fun.workTime()) {
					try {
						msg = db.executeQuery1(sql, "Nije bilo uplata celog dana", columns);
						msg = fun.setUTF8(msg);
						fun.sendEmail(msg, "resivojee@gmail.com", "Sumarno po lokacijama i tipu");
						fun.sendEmail(msg, "presidente.ks@gmail.com", "Sumarno po lokacijama i tipu");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				Thread.sleep(21600000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
