package Presidente.TransactionApi;

import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

public class transactionsReport extends Thread {

	DbFunctions db = new DbFunctions();
	Functions fun = new Functions();
	ConstError ce = new ConstError();
	static String msg = "";
	static String sql;
	static String[] columns = { "ukupno", "slot_club_id", "path" };
	
	public static String createQuery() {
		LocalTime now = LocalTime.now();
		String sql = "";
		String startDateString;
		String endDateString;
		ZoneId defaultZoneId = ZoneId.systemDefault();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		if (now.isAfter(LocalTime.parse("08:00:00"))) {
			LocalDate startDate = LocalDate.now();
			LocalDate endDate = LocalDate.now().plusDays(1);
			
			startDateString = dateFormat.format(Date.from(startDate.atStartOfDay(defaultZoneId).toInstant()));
			endDateString = dateFormat.format(Date.from(endDate.atStartOfDay(defaultZoneId).toInstant()));
			
		} else {
			LocalDate startDate = LocalDate.now().minusDays(1);
			LocalDate endDate = LocalDate.now();
			
			startDateString = dateFormat.format(Date.from(startDate.atStartOfDay(defaultZoneId).toInstant()));
			endDateString = dateFormat.format(Date.from(endDate.atStartOfDay(defaultZoneId).toInstant()));
		}
		
		sql = "SELECT SUM(transaction_amount) as ukupno, slot_club_id, public.transaction_types.path FROM public.transactions INNER JOIN public.transaction_types ON public.transactions.transaction_types = public.transaction_types.transaction_types WHERE transaction_time BETWEEN DATE('" + startDateString + "') + TIME '07:00' AND DATE('" + endDateString + "') + TIME '07:00' GROUP BY slot_club_id,  public.transaction_types.path";
		
		return sql;
	}
	
	
	
	
	public void run() {
		while(true) {
			try {
				if(fun.workTime()) {
					try {
						sql = createQuery();
						String slotClubId = null;
						JSONArray slotClubsWithData = new JSONArray();
						JSONObject item = new JSONObject();
						ResultSet rs = db.executeQuery4(sql);
						while (rs.next()) {
							//if(slotClubsWithData.otpString(rs.getString("slot_club_id")){}
							item.put(rs.getString("slot_club_id"), new JSONObject());
							slotClubsWithData.put(item);
							item.clear();
						}
						msg = fun.setUTF8(msg);
						fun.sendEmail(msg, "presidenteapp@yahoo.com", "Sumarno po lokacijama i tipu");
						fun.sendEmail(msg, "presidente.ks@gmail.com", "Sumarno po lokacijama i tipu");
						createQuery();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				Thread.sleep(0);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}}
