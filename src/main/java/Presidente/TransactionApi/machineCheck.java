package Presidente.TransactionApi;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class machineCheck extends Thread {

	DbFunctions db = new DbFunctions();
	Functions fun = new Functions();
	static Connection lConn;
	static String msg;
	static String sql = "select * from machines where not id_number in (SELECT distinct machine_num_id FROM public.transactions WHERE transaction_time BETWEEN NOW() - INTERVAL '6 HOURS' AND NOW())";
	static String[] columns = {"sticker_number", "id_number", "slot_club_id", "producer_serial_number", "producer_name", "mesec_i_godina_proizvodnje", "vlasnistvo", "tipaparata", "tipigre", "funkcionalnostsoftvera"};
	
	public void run() {
		while (true) {
			try {
				try {
					try {
						msg = db.executeQuery1(sql, "Svi aparati salju podatke", columns);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					fun.sendEmail(msg);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (SecurityException e) {
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
