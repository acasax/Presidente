package Presidente.TransactionApi;

import java.text.ParseException;

public class paymentCheck extends Thread {

	DbFunctions db = new DbFunctions();
	Functions fun = new Functions();
	static String msg;
	static String sql = "SELECT SUM(transaction_amount) as ukupno, slot_club_id, transaction_types FROM public.transactions WHERE DATE(transaction_time) = CURRENT_DATE GROUP BY slot_club_id,  transaction_types";
	static String[] columns = {"ukupno", "slot_club_id", "transaction_types"};
			
	public void run() {
		try {
			if (!fun.workTime()) {
				while (true) {
					try {
						msg = db.executeQuery1(sql, "Nije bilo uplata celog dana", columns);
						msg = fun.setUTF8(msg);
						fun.sendEmail(msg, "resivojee@gmail.com", "Sumarno po lokacijama i tipu");
						try {
							Thread.sleep(86400000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
