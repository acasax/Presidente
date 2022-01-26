package Presidente.TransactionApi;

import java.text.ParseException;

public class apiUuidStatus extends Thread {
	DbFunctions db = new DbFunctions();
	Functions fun = new Functions();
	static String msg;
	static String sql = "SELECT * FROM public.transactions WHERE api_uuid is null ORDER BY transaction_time DESC limit 1 ";
	static String[] columns = { "transaction_id" };

	public void run() {
	
		while(true) {
			try {
				if(fun.workTime()) {
					try {
						msg = db.executeQuery2(sql, "Slanje ka upravi je prestalo", columns);
						if(msg == "Slanje ka upravi je prestalo") {
							msg = fun.setUTF8(msg);
							fun.sendEmail(msg, "resivojee@gmail.com", "SRANJE SE DESAVA NEKO SA SLANJEM KA UPRAVI");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				Thread.sleep(300000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}