package Presidente.TransactionApi;

import java.sql.Connection;
import java.text.ParseException;

public class locationCheck extends Thread {

	DbFunctions db = new DbFunctions();
	Functions fun = new Functions();
	static String msg;
	
	private Connection conn;
	private boolean isDev;
	 
	public locationCheck(Connection lConn, boolean isDev) {
		// TODO Auto-generated constructor stub
		this.conn = lConn;
		this.isDev = isDev;
	}

	public void run() {
		try {
			while (true) {
				if (fun.workTime()) {
					try {
						msg = db.executeQuery1(sqlConsts.sqlLocationWorkCheck, "Sve lokacije salju podatke", sqlConsts.columnsLocationWorkCheck, conn);
						fun.sendEmail(msg, "presidenteapp@yahoo.com", "Lokacije koje nisu slale podatke");
						if(!isDev) {
							fun.sendEmail(msg, "dusan@presidente.rs", "Lokacije koje nisu slale podatke");
							fun.sendEmail(msg, "sladja.djokic@presidente.rs", "Lokacije koje nisu slale podatke");
							fun.sendEmail(msg, "marija.milic@presidente.rs", "Lokacije koje nisu slale podatke");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				try {
					Thread.sleep(7200000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
