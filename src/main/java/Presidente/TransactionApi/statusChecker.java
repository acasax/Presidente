package Presidente.TransactionApi;

import java.sql.Connection;
import java.text.ParseException;

public class statusChecker extends Thread {

	DbFunctions db = new DbFunctions();
	Functions fun = new Functions();
	static String msg;
	
	private Connection conn;

	public statusChecker(Connection lConn) {
		// TODO Auto-generated constructor stub
		this.conn = lConn;
	}
	
	public void run() {
		try {
			while (true) {
				if (fun.workTime()) {
					try {
						msg = db.executeProcedure(sqlConsts.sqlStatusChecker, conn);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				try {
					Thread.sleep(300000);
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
