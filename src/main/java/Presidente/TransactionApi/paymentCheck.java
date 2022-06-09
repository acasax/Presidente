package Presidente.TransactionApi;

import java.text.ParseException;

public class paymentCheck extends Thread {

	DbFunctions db = new DbFunctions();
	Functions fun = new Functions();
	static String msg;
	static String sql = "SELECT SUM(public.transactions.transaction_amount) as suma, count(public.transactions.transaction_id) as brojUplata, public.transactions.slot_club_id, public.transaction_types.path FROM public.transactions INNER JOIN public.transaction_types ON public.transactions.transaction_types = public.transaction_types.transaction_types WHERE public.transactions.transaction_time BETWEEN CURRENT_DATE - INTEGER '1' + TIME '06:45:59' AND CURRENT_DATE + TIME '03:59:59' GROUP BY public.transactions.slot_club_id, public.transaction_types.path";
	static String[] columns = { "suma", "slot_club_id", "brojUplata", "path" };

	
	public void run() {
		while(true) {
			try {
				if(!fun.workTime()) {
					try {
						msg = db.executeQuery1(sql, "Nije bilo uplata celog dana", columns);
						msg = fun.setUTF8(msg);
						fun.sendEmailYahho(msg, "presidenteapp@yahoo.com", "Sumarno po lokacijama i tipu");
						fun.sendEmailYahho(msg, "presidente.ks@yahoo.com", "Sumarno po lokacijama i tipu");
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
