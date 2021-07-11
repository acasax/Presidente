package Presidente.TransactionApi;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import org.json.JSONObject;

public class spStart extends Thread {

	static String url = "jdbc:postgresql://65.21.110.211:5432/accounting";
	static String user = "presidente";
	static String password = "test";
	static Object pgconn;
	static Connection lConn;
	static String spWithStatus0;
	static String reportIndex;
	static ArrayList<spProcessing> lista = new ArrayList<>();

	Functions fun = new Functions();
	DbFunctions db = new DbFunctions();

	// Da li postoji proces sa zadatim report index koji radi
	public static spProcessing nadjiProcessing(String transactionId) {
		for (int i = 0; i < lista.size(); i++) {
			if (lista.get(i).getReportIndex().equals(transactionId)) {
				spProcessing badProcess = lista.get(i);
				lista.remove(i);
				return badProcess;
			}
		}
		return null;
	}

	// Prekida proces
	public static void prekini(spProcessing p) {
		p.interrupt();
	}

	@Override
	public void run() {
		while(true) {
			try {

				db.asyconnect(url, user, password);
				lConn = DriverManager.getConnection(url, user, password);
				//Slanje zahteva na putanju slot-periodic
				//
				fun.sendSlotPeriodicWithStatus0(lConn, reportIndex, db, lista);

			} catch (SecurityException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				try {
					fun.createLog(e.getMessage());
				} catch (SecurityException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				try {
					fun.createLog(e.getMessage());
				} catch (SecurityException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			try {
				//cekaj 5 minuta pa uradi ponovo
				//
				Thread.sleep(300000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				try {
					fun.createLog(e.getMessage());
				} catch (SecurityException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		
	}

}
