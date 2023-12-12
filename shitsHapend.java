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
						msg = db.executeQuery1(sqlConsts.sqlShitsHapend, "Nije stiglo nista u bazu zadnjih 25 minuta", sqlConsts.columnsShitsHapend, conn);
						if(msg == "Nije stiglo nista u bazu zadnjih 25 minuta") {
							fun.sendEmail(msg, "presidenteapp@yahoo.com", "Nije stiglo nista u bazu zadnjih 25 minuta");
						}
						msg = db.executeQuery1(sqlConsts.sqlShitsHapend1, "Nije poslato nista upravi zadnjih 25 minuta", sqlConsts.columnsShitsHapend, conn);
						if(msg == "Nije poslato nista upravi zadnjih 25 minuta") {
							fun.sendEmail(msg, "presidenteapp@yahoo.com", "Nije poslato nista upravi zadnjih 25 minuta");
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
