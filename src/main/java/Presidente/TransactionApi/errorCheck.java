package Presidente.TransactionApi;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class errorCheck extends Thread {
	DbFunctions db = new DbFunctions();
	Functions fun = new Functions();
	static Connection lConn;
	/*static String url = "jdbc:postgresql://93.87.76.160:5432/accounting";
	static String user = "presidente";
	static String password = "testpass";*/
	
	static String url = "jdbc:postgresql://93.87.76.160:5432/accounting";
	static String user = "presidente";
	static String password = "testpass";
	static String cronError;
	static int reportIndex;
	static String spWorkStatus;

	public errorCheck(Connection lConn) {
		super();
		this.lConn = lConn;
	}
	
	public void run() {
		while (true) {
			try {
				cronError = fun.getCronError(db, lConn);
				if (cronError.equals("1")) {
					// proverava da li u bazi ima izvestaja sa statusom 11
					fun.sendEmail("cron error je 1");
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
}
