package Presidente.TransactionApi;

import java.io.IOException;
import java.sql.Connection;
import java.text.ParseException;

public class spErrorCheck extends Thread {
	DbFunctions db = new DbFunctions();
	Functions fun = new Functions();
	
	static String cronError;
	static int reportIndex;
	static String spWorkStatus;

	public void run() {
		try {
			if(fun.workTime()) {
				while (true) {
					try {
						cronError = fun.getCronError(db);
						if (cronError.equals("1")) {
							// proverava da li u bazi ima sn izvestaja sa statusom 1
							fun.sendEmailYahho("cron error je 1", "presidenteapp@yahoo.com", "report index, slot preiodic error");
						}
					} catch (SecurityException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					try {
						Thread.sleep(60000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
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
