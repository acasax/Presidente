package Presidente.TransactionApi;

import java.sql.Connection;
import java.text.ParseException;

public class badTransactions extends Thread {
	DbFunctions db = new DbFunctions();
	Functions fun = new Functions();
	static String msg;
	
	private Connection conn;
	
	public badTransactions(Connection lConn) {
		// TODO Auto-generated constructor stub
		this.conn = lConn;
	}

	public void run() {
		try {
			while (true) {
				if (fun.workTime()) {
					try {
						msg = db.executeQuery1(sqlConsts.sqlBadTransaction, "Losa transakcija", sqlConsts.columnBadTransaction, conn);
						if(msg != "") {
							msg = fun.setUTF8(msg);
							fun.sendEmail(msg, "presidenteapp@yahoo.com", "Losa transakcija");
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
