package Presidente.TransactionApi;

import java.text.ParseException;

public class apiUuidStatus extends Thread {
	DbFunctions db = new DbFunctions();
	Functions fun = new Functions();
	static String msg;
	static String sql = "SELECT count(transaction_id) FROM public.transactions where status = 0";
	static String[] columns = { "count" };

	public void run() {
	
		while(true) {
			try {
				if(fun.workTime()) {
					try {
						msg = db.executeQuery2(sql, "Slanje ka upravi je ok", columns);
						//msg = fun.setUTF8(msg);
						fun.sendEmailYahho(msg, "presidenteapp@yahoo.com", "SRANJE SE DESAVA NEKO SA SLANJEM KA UPRAVI");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				Thread.sleep(3600000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}
