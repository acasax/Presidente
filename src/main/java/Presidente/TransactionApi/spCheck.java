package Presidente.TransactionApi;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;

public class spCheck extends Thread {
	DbFunctions db = new DbFunctions();
	Functions fun = new Functions();
	
	static String spWithStatus11;
	static int reportIndex;
	static String spWorkStatus;
	
    private Connection conn;
    
    public spCheck(Connection conn) {
        this.conn = conn;
    }

	public void run() {
		try {
			if(fun.workTime()) {
				while (true) {
					try {
						// proverava da li u bazi ima izvestaja sa statusom 11
						spWithStatus11 = db.executeFunction("SELECT public.get_json_sp_by_status(11)", "get_json_sp_by_status", conn);
						//kreira parametre
						if (spWithStatus11 != null ) {
							reportIndex = fun.getReportIndex(spWithStatus11, "s"); // uzima reportindex za taj
							spWorkStatus = fun.getSpWorkStatus(reportIndex, db, conn); // proveraba procedurom da li ima procesinga
						}
						
						// ubija koji nerade kako treba
						if (spWithStatus11 != null && Integer.parseInt(spWorkStatus) == 1) {
							spProcessing badProcessing = spStart.nadjiProcessing(reportIndex);
							spStart.prekini(badProcessing);
						}
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
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
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
