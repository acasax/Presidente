package Presidente.TransactionApi;

import java.sql.Connection;
import java.text.ParseException;

public class slotPeriodicCheck extends Thread {

	DbFunctions db = new DbFunctions();
	Functions fun = new Functions();
	static String msg;
	
    private Connection conn;
    private boolean isDev;
    
    public slotPeriodicCheck(Connection conn, boolean isDev) {
        this.conn = conn;
        this.isDev = isDev;
    }

	public void run() {
	
		while(true) {
			try {
				if(!fun.workTime()) {
					try {
						msg = db.executeQuery2(sqlConsts.sqlSlotPeriodicCheck, "Brojaci", sqlConsts.columnsSlotPeriodicCheck, conn);
						msg = fun.setUTF8(msg);
						msg = fun.slotPriodicCheckString(msg);
						fun.sendEmail(msg, "presidenteapp@yahoo.com", "Poslednji brojaci");
						if(!isDev) {
							fun.sendEmail(msg, "presidente.ks@gmail.com", "Poslednji brojaci");	
						}
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
