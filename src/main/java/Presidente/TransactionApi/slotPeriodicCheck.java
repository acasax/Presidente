package Presidente.TransactionApi;

import java.text.ParseException;

public class slotPeriodicCheck extends Thread {

	DbFunctions db = new DbFunctions();
	Functions fun = new Functions();
	static String msg;
	static String sql = "SELECT * FROM public.slot_periodic_h ORDER BY report_index DESC LIMIT 1";
	static String[] columns = { "api_json" };

	public void run() {
	
		while(true) {
			try {
				if(!fun.workTime()) {
					try {
						msg = db.executeQuery2(sql, "Brojaci", columns);
						msg = fun.setUTF8(msg);
						msg = fun.slotPriodicCheckString(msg);
						fun.sendEmailYahho(msg, "presidenteapp@yahoo.com", "Poslednji brojaci");
						fun.sendEmailYahho(msg, "presidente.ks@gmail.com", "Poslednji brojaci");
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
