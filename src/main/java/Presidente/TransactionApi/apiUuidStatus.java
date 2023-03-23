package Presidente.TransactionApi;

import java.text.ParseException;

public class apiUuidStatus extends Thread {
	DbFunctions db = new DbFunctions();
	Functions fun = new Functions();
	static String msg;

	public void run() {
	
		while(true) {
			try {
				if(fun.workTime()) {
					try {
						msg = db.executeQuery2(sqlConsts.sqlApiUuidStatus, "Slanje ka upravi je ok", sqlConsts.columnsApiUuidStatus);
						fun.sendEmail(msg, "presidenteapp@yahoo.com", "Ima transakcija koje nisu otisle");
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
