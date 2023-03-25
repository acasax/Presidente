package Presidente.TransactionApi;

import java.sql.Connection;
import java.text.ParseException;

public class paymentCheck extends Thread {

	DbFunctions db = new DbFunctions();
	Functions fun = new Functions();
	static String msg;
	
    private Connection conn;
	    
    public paymentCheck(Connection conn) {
		this.conn = conn;
    }
	
	public void run() {
		while(true) {
			try {
				if(!fun.workTime()) {
					try {
						msg = db.executeQuery1(sqlConsts.sqlPaymentCheck, "Nije bilo uplata celog dana", sqlConsts.columnsPaymentCheck, conn);
						msg = fun.setUTF8(msg);
						fun.sendEmail(msg, "presidenteapp@yahoo.com", "Sumarno po lokacijama i tipu");
						fun.sendEmail(msg, "presidente.ks@gmail.com", "Sumarno po lokacijama i tipu");
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
