package Presidente.TransactionApi;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

//Don't work until api41
public class machineCheck extends Thread {

	DbFunctions db = new DbFunctions();
	Functions fun = new Functions();
	static String msg;
	
    private Connection conn;
    private boolean isDev;
	    
	public machineCheck(Connection conn, boolean isDev) {
	    this.conn = conn;
	    this.isDev = isDev;
	}
	    
	public void run() {
		while (true) {
			try {
				try {
					try {
						msg = db.executeQuery1(sqlConsts.sqlMachineCheck, "Svi aparati salju podatke", sqlConsts.columnsMachineCheck, conn);
						fun.sendEmail(msg, "presidenteapp@yahoo.com", "Aparati koje nisu slale podatke");
						if(!isDev) {
							fun.sendEmail(msg, "dusan@presidente.rs", "APARATI koje nisu slale podatke");
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//fun.sendEmail(msg);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (SecurityException e) {
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
