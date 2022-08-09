package Presidente.TransactionApi;

import java.text.ParseException;

public class badTransactions extends Thread {
	DbFunctions db = new DbFunctions();
	Functions fun = new Functions();
	static String msg;
	static String sql = "SELECT * FROM public.bad_transactions where status = 0";
	static String[] columns = { "bed_transactions_id" };

	public void run() {
		try {
			while (true) {
				if (fun.workTime()) {
					try {
						msg = db.executeQuery1(sql, "Losa transakcija", columns);
						if(msg != "") {
							msg = fun.setUTF8(msg);
							fun.sendEmailYahho(msg, "presidenteapp@yahoo.com", "Losa transakcija");
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
