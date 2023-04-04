package Presidente.TransactionApi;

import java.sql.Connection;
import java.text.ParseException;

public class shitsHapend extends Thread {
	DbFunctions db = new DbFunctions();
	Functions fun = new Functions();
	static String msg;
	
    private Connection conn;
    
    public shitsHapend(Connection conn) {
        this.conn = conn;
    }

	public void run() {
		try {
			while (true) {
				if (fun.workTime()) {
					try {
						msg = db.executeQuery1(sqlConsts.sqlShitsHapend, "Sranje neko je pocelo", sqlConsts.columnsShitsHapend, conn);
						if(msg != "Sranje neko je pocelo") {
							//msg = fun.setUTF8(msg);
							fun.sendEmail(msg, "presidenteapp@yahoo.com", "SRANJE SE DESAVA NEKO");
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
