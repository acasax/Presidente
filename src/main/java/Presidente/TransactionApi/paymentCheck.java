package Presidente.TransactionApi;
import java.util.TimerTask;

public class paymentCheck extends TimerTask {

	DbFunctions db = new DbFunctions();
	Functions fun = new Functions();
	static String msg;
	static String sql = "SELECT SUM(transaction_amount) as ukupno, slot_club_id, transaction_types FROM public.transactions WHERE DATE(transaction_time) = CURRENT_DATE GROUP BY slot_club_id,  transaction_types";
	static String[] columns = { "ukupno", "slot_club_id", "transaction_types" };

	public void run() {
		try {
			msg = db.executeQuery1(sql, "Nije bilo uplata celog dana", columns);
			msg = fun.setUTF8(msg);
			fun.sendEmail(msg, "resivojee@gmail.com", "Sumarno po lokacijama i tipu");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
