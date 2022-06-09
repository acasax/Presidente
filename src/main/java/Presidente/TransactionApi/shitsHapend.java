package Presidente.TransactionApi;

import java.text.ParseException;

public class shitsHapend extends Thread {
	DbFunctions db = new DbFunctions();
	Functions fun = new Functions();
	static String msg;
	static String sql = "SELECT transaction_id FROM public.transactions WHERE transaction_time BETWEEN NOW() - INTERVAL '25 minutes' AND NOW() order by transaction_time limit 1";
	static String[] columns = { "transaction_id" };

	public void run() {
		try {
			while (true) {
				if (fun.workTime()) {
					try {
						msg = db.executeQuery1(sql, "Sranje neko je pocelo", columns);
						if(msg == "Sranje neko je pocelo") {
							msg = fun.setUTF8(msg);
							fun.sendEmailYahho(msg, "presidenteapp@yahoo.com", "SRANJE SE DESAVA NEKO");
						}
						try {
							Thread.sleep(1500000);
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
