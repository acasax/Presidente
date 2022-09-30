package Presidente.TransactionApi;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.io.Charsets;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Functions {

	ConstError ce = new ConstError();

	// Funkcija koja uzima iz JSON-a samo transaction_id
	//
	public String getTransansactionId(String JSON, String Status) throws SecurityException, IOException {
		// Status s stiglo iz baze samo json
		if (Status == "s") {
			String jsonString = JSON;
			String transactionId = "";
			try {
				JSONObject obj = new JSONObject(jsonString);
				if (obj.isNull("transaction_id")) {
					return transactionId;
				}
				return transactionId = obj.getString("transaction_id");
			} catch (JSONException e) {
				createLog("U ovom JSON-u nema polja transaction_id" + e.getMessage());
				return "U ovom JSON-u nema polja transaction_id";
			}

		} else {
			String transactionId = "";
			try {
				String str = JSON.substring(JSON.indexOf("{"));
				String jsonString = str;
				JSONObject obj = new JSONObject(jsonString);
				if (obj.isNull("transaction_id")) {
					return transactionId;
				}
				return transactionId = obj.getString("transaction_id");
			} catch (JSONException e) {
				createLog("U ovom JSON-u nema polja transaction_id" + e.getMessage());
				return "U ovom JSON-u nema polja transaction_id";
			}

		}
	}

	// Funkcija koja uzima path na osnovu tipa
	//
	public String getTransansactionPath(String JSON, DbFunctions db, Boolean status) throws SecurityException, IOException{
		String path = null;
		if(status) {
			String transaction_type = getParamFromJson(JSON, "transaction_types");
			path = ce.transactionPathByType(Integer.parseInt(transaction_type));
		} else {
			path = getParamFromJson(JSON, "path");
		}
		return path;
	}

	// Funkcija koja uzima odredjeni parametar iz JSON-a
	//
	public String getParamFromJson(String JSON, String Param) throws SecurityException, IOException {
		String jsonString = JSON;
		double paramValueD;
		String paramValue;
		DecimalFormat f = new DecimalFormat("##.##");
		JSONObject obj = new JSONObject(jsonString);
		switch (Param) {
		case "transaction_amount":
			try {
				if (obj.isNull(Param)) {
					return null;
				}
				paramValueD = obj.getDouble(Param);
				return String.valueOf(f.format(paramValueD));
			} catch (JSONException e) {
				createLog(ce.getParamFromJsonDuable + "JSON" + JSON + "Parametar" + Param);
				return null;
			}
		case "transaction_withdraw_amount":
			try {
				if (obj.isNull(Param)) {
					return null;
				}
				paramValueD = obj.getDouble(Param);
				return String.valueOf(f.format(paramValueD));
			} catch (JSONException e) {
				createLog(ce.getParamFromJsonDuable + "JSON" + JSON + "Parametar" + Param);
				return null;
			}
		case "transaction_types":
			try {
				if (obj.isNull(Param)) {
					return null;
				}
				paramValueD = obj.getInt(Param);
				return String.valueOf(f.format(paramValueD));
			} catch (JSONException e) {
				createLog(ce.getParamFromJsonDuable + "JSON" + JSON + "Parametar" + Param);
				return null;
			}
		default:
			try {
				if (obj.isNull(Param)) {
					return null;
				}
				paramValue = obj.getString(Param);
				return paramValue;
			} catch (JSONException e) {
				if (!Param.equals("error")) {
					createLog(ce.getParamFromJson + "JSON" + JSON + "Parametar" + Param);
				}
				return null;
			}
		}

	}

	// Funkcija koja proveraba da li JSON ima sva polja koja su potrebna za
	// odredjenu putanju
	//
	public JSONObject checkJSONforSend(String JSON, String path, DbFunctions db, Boolean status) throws SecurityException, IOException, SQLException {

		// Uzimanje podataka iz JSON-a
		//
		String transaction_time = getParamFromJson(JSON, "transaction_time");
		String transaction_id = getParamFromJson(JSON, "transaction_id");
		String transaction_amount = getParamFromJson(JSON, "transaction_amount");
		String transaction_type = null;
		if(status) {
			transaction_type = getParamFromJson(JSON, "transaction_types");
		} else {
			transaction_type = ce.transactionTybeByString(getParamFromJson(JSON, "transaction_type"), getParamFromJson(JSON, "path")).toString();
		}
		String slot_club_id = getParamFromJson(JSON, "slot_club_id");
		String sticker_no = null;
		if(status) {
			String machine_id_number = getParamFromJson(JSON, "machine_num_id");
			if(machine_id_number != null || machine_id_number != "") {
				String strickerNumberQuery = "SELECT sticker_number FROM public.machines where id_number = '" + machine_id_number.trim() + "'";
				String[] columns = {"sticker_number"};
				
				sticker_no = db.executeQuery3(strickerNumberQuery, columns);
			} 
		} else {
			sticker_no = getParamFromJson(JSON, "sticker_number");
		}

		JSONObject transactionBody = new JSONObject();

		// Provera da li su svi parametri tu
		//
		if (transaction_time == null || transaction_id == null || transaction_amount == null || transaction_type == null
				|| slot_club_id == null || sticker_no == null) {
			transactionBody.put("error", "JSON koji je stigao u aplikaciju nema sve potrebne elemente za slanja");
			return transactionBody;
		}
		
		//Provera da li je vreme kako treba 
		//
		//Date date = new Date();
		Timestamp timestamp1 = null;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		ZoneId defaultZoneId = ZoneId.systemDefault();
		LocalDate datel = LocalDate.now().minusYears(1);
		String date = df.format(Date.from(datel.atStartOfDay(defaultZoneId).toInstant()));
		Timestamp timestamp2  = Timestamp.valueOf(date);
		
		try {
			Date date1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(transaction_time);
			String transactionTime = df.format(date1);
			timestamp1 = Timestamp.valueOf(transactionTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		
		if(timestamp1.before(timestamp2) ) {
			transactionBody.put("error", "Vreme za slanje nije dobro. transaction id" + transaction_id + "lokacija: " + slot_club_id);
			return transactionBody;
		}

		// Parsiranje podataka u potreban format
		//
		Double p_transaction_amount = Double.valueOf(transaction_amount);

		switch (path) {
		case "slot/deposit":
			transactionBody.put("transaction_time", transaction_time);
			transactionBody.put("transaction_id", transaction_id);
			transactionBody.put("transaction_amount", p_transaction_amount);
			transactionBody.put("transaction_type", transaction_type);
			transactionBody.put("slot_club_id", slot_club_id);
			transactionBody.put("sticker_no", sticker_no);
			if (p_transaction_amount > ce.maxDeposit) {
				//transactionBody.put("send_status", "Uplata nije za slanje. ID: " + transaction_id);
				String macAddress = getMacAddressOfMachines(sticker_no, db);
				sendEmailYahho("Postoji uplata veca od " + String.valueOf(ce.maxDeposit) + " ID: " + transaction_id
						+ " Slot klub id: " + ce.slotClubIdFromSlotClubSid(slot_club_id) + " Aparat: " + sticker_no + " Mak adresa: " + macAddress,
						" presidenteapp@yahoo.com", "Velika uplata");
			}
			return transactionBody;
		case "slot/withdraw":
			transactionBody.put("transaction_time", transaction_time);
			transactionBody.put("transaction_id", transaction_id);
			transactionBody.put("transaction_amount", p_transaction_amount);
			transactionBody.put("transaction_type", transaction_type);
			transactionBody.put("slot_club_id", slot_club_id);
			transactionBody.put("sticker_no", sticker_no);
			if (p_transaction_amount > ce.maxWithdraw) {
				//transactionBody.put("send_status", "Islata nije za slanje. ID: " + transaction_id);
				String macAddress = getMacAddressOfMachines(sticker_no, db);
				sendEmailYahho("Postoji isplata veca od " + String.valueOf(ce.maxWithdraw) + " ID: " + transaction_id
						+ " Slot klub id: " + ce.slotClubIdFromSlotClubSid(slot_club_id) + " Aparat: " + sticker_no + " Mak adresa: " + macAddress,
						"presidenteapp@yahoo.com", "Velika isplata");
			}
			return transactionBody;
		case "slot/jackpot":
			// Ovde je zato sto postoji samo za ovu rutu
			//
			// String transaction_withdraw_amount = getParamFromJson(JSON,
			// "transaction_withdraw_amount");
			// Double p_transaction_withdraw_amount =
			// Double.valueOf(transaction_withdraw_amount); // Konvertovanje u
			// potrebni tip
			transactionBody.put("transaction_time", transaction_time);
			transactionBody.put("transaction_id", transaction_id);
			transactionBody.put("transaction_amount", p_transaction_amount);
			transactionBody.put("transaction_type", transaction_type);
			transactionBody.put("slot_club_id", slot_club_id);
			transactionBody.put("sticker_no", sticker_no);
			transactionBody.put("transaction_withdraw_amount", 0);
			String macAddress = getMacAddressOfMachines(sticker_no, db);
			sendEmailYahho("ID: " + transaction_id + "Slot klub id: " + ce.slotClubIdFromSlotClubSid(slot_club_id)
					+ "Aparat: " + sticker_no + "Mak adresa: " + macAddress + "Iznos: " + p_transaction_amount, "presidenteapp@yahoo.com", "Jackpot");
				return transactionBody;
		case "slot/rollback":
			// Ovde je zato sto postoji samo za ovu rutu
			//
			String rollback_transaction_id = getParamFromJson(JSON, "rollback_transaction_id");
			transactionBody.put("transaction_time", transaction_time);
			transactionBody.put("transaction_id", transaction_id);
			transactionBody.put("transaction_amount", p_transaction_amount);
			transactionBody.put("transaction_type", transaction_type);
			transactionBody.put("slot_club_id", slot_club_id);
			transactionBody.put("sticker_no", sticker_no);
			transactionBody.put("rollback_transaction_id", rollback_transaction_id);
			return transactionBody;
		default:
			transactionBody.put("error", "Putanja koju ste poslali u funkciju nije dobra. Putanja: " + path);
			return transactionBody;
		}
	}
	
	//Uzimanje mac adrese aparata 
	//
	public String getMacAddressOfMachines(String sn, DbFunctions db) throws SecurityException, IOException {
		String macAddress = "";
		try {
			String sql = "SELECT * FROM public.machines WHERE sticker_number = " + "'" + sn + "'";
			String[] columns = { "id_number" };
			macAddress = db.executeQuery2(sql, "Nema izabrani sn broj" + sn, columns);
			return macAddress;
		} catch (SQLException e) {
			createLog("getMacAddressOfMachines" + e.getMessage() + "SN ERROR" + macAddress);
			return "macAddress nije kako treba";
		}
	}

	// Funkcija za proveru cekanja do sledeceg slanja
	//
	public String getApiCounter(String transaction_id, DbFunctions db) throws SecurityException, IOException {
		try {
			String apiCounter = db.executeFunction("SELECT public.get_api_counter('" + transaction_id + "')",
					"get_api_counter");
			if (Integer.parseInt(apiCounter) < 3) {
				return "60000";
			} else {
				return "3600000";
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			createLog("Api kaunter nije kako treba" + e.getMessage());
			return "Api kaunter nije kako treba";
		}
	}

	public String getWorkStatus(String transaction_id, DbFunctions db) throws SecurityException, IOException {
		try {
			String workStatus = db.executeFunction("SELECT public.get_transaction_exe_status('" + transaction_id + "')",
					"get_transaction_exe_status");
			return workStatus;
		} catch (SQLException e) {
			createLog("work status nije kako treba" + e.getMessage());
			return "work status nije kako treba";
		}
	}

	// Funkcija koja kreira log fajlove sa greskom
	//
	public void createLog(String msg) throws SecurityException, IOException {
		String path = "logs";
		// Creating a File object
		File file = new File(path);
		// Creating the directory
		file.mkdir();
		DateFormat FileNameFormat = new SimpleDateFormat("dd-M-yyyy_hh-mm-ss");
		Date FileName = new Date();
		FileHandler handler = new FileHandler("logs/" + "log_" + FileNameFormat.format(FileName) + ".log");

		Logger logger = Logger.getLogger("ResivoJe");
		logger.addHandler(handler);

		logger.warning(msg);

	}

	// Funkcija koja kreira log fajlove sa greskom za bazu
	//
	public void createLogDb(String msg) throws SecurityException, IOException {
		String path = "logsDb";
		// Creating a File object
		File file = new File(path);
		// Creating the directory
		file.mkdir();

		DateFormat FileNameFormat = new SimpleDateFormat("dd-M-yyyy_hh-mm-ss");
		Date FileName = new Date();
		FileHandler handler = new FileHandler("logsDb/" + "log_" + FileNameFormat.format(FileName) + ".log");

		Logger logger = Logger.getLogger("ResivoJe");
		logger.addHandler(handler);

		logger.warning(msg);

	}

	// Funkcija za slanje e-mail
	//
	public void sendEmail(String msg, String emailTo, String subject) {
		// Recipient's email ID needs to be mentioned.
		String to = emailTo;

		// Sender's email ID needs to be mentioned
		String from = "acasax@gmail.com";

		// Assuming you are sending email from through gmails smtp
		String host = "smtp.gmail.com";

		// Get system properties
		Properties properties = System.getProperties();

		// Setup mail server
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");

		// Get the Session object.// and pass username and password
		Session session = Session.getInstance(properties, new javax.mail.Authenticator() {

			protected PasswordAuthentication getPasswordAuthentication() {

				return new PasswordAuthentication("acasax@gmail.com", "cgbjqlhqsylqcmsp");

			}

		});

		// Used to debug SMTP issues
		session.setDebug(true);

		try {
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);

			// Set From: header field of the header.
			message.setFrom(new InternetAddress(from));

			// Set To: header field of the header.
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

			// Set Subject: header field
			message.setSubject(subject);

			// Now set the actual message
			message.setText(msg);

			Transport.send(message);
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}

	}

	// Funkcija za slanje e-mail Saletu
	//
	public void sendEmailYahho(String msg, String emailTo, String subject) {
		// Recipient's email ID needs to be mentioned.
		String to = emailTo;

		// Sender's email ID needs to be mentioned
		String from = "presidentesaxapp@yahoo.com";

		// Assuming you are sending email from through gmails smtp
		String host = "smtp.mail.yahoo.com";

		// Get system properties
		Properties properties = System.getProperties();

		// Setup mail server
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "587");
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.auth", "true");

		// Get the Session object.// and pass username and password
		Session session = Session.getInstance(properties, new javax.mail.Authenticator() {

			protected PasswordAuthentication getPasswordAuthentication() {

				return new PasswordAuthentication("mrsax23@yahoo.com", "whwsifdwrnxxqump");

			}

		});

		// Used to debug SMTP issues
		session.setDebug(true);

		try {
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);

			// Set From: header field of the header.
			message.setFrom(new InternetAddress(from));

			// Set To: header field of the header.
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

			// Set Subject: header field
			message.setSubject(subject);

			// Now set the actual message
			message.setText(msg);

			Transport.send(message);
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}

	}

	// Funkcija koja proverava da li postoji folder sa logovima
	//
	public void checkIsLogExist(String path) {

		String newLine = System.getProperty("line.separator");
		String msg;
		File directory = new File(path);

		String[] flist = directory.list();
		int flag = 0;
		if (flist == null) {
			msg = "Folder logs je prazan.";
		} else {
			msg = "Pronadjeni log fajlovi sa imenima:" + newLine;
			// Linear search in the array
			for (int i = 0; i < flist.length; i++) {
				String filename = flist[i];
				if (filename.endsWith(".log")) {
					msg = msg + filename + newLine;
					flag = 1;
				}
			}
			sendEmail(msg, "presidenteapp@yahoo.com", "Greska");
		}

		if (flag == 0) {
			msg = "Nema pronadjenih log fajlova";
			sendEmail(msg, "presidenteapp@yahoo.com", "Sve ok je bre!!!");
		}

	}

	// Funkcija koja uzima iz JSON-a samo report index
	//
	public int getReportIndex(String JSON, String Status) throws SecurityException, IOException {
		// Status s stiglo iz baze samo json
		int report_index = 0;
		if (Status == "s") {
			String jsonString = JSON;

			try {

				final JSONObject jsonObject = new JSONObject(jsonString);
				final JSONArray machines = jsonObject.getJSONArray("machines");
				for (int i = 0; i < machines.length(); i++) {
					final JSONObject machine = machines.getJSONObject(i);
					report_index = machine.getInt("report_index");
				}
				return report_index;
			} catch (JSONException e) {
				createLog("U ovom JSON-u nema polja report index" + e.getMessage());
				return 0;
			}

		} else {
			try {
				String str = JSON.substring(JSON.indexOf("{"));
				String jsonString = str;
				final JSONObject jsonObject = new JSONObject(jsonString);
				final JSONArray machines = jsonObject.getJSONArray("machines");
				for (int i = 0; i < machines.length(); i++) {
					final JSONObject machine = machines.getJSONObject(i);
					report_index = machine.getInt("report_index");
				}
				return report_index;
			} catch (JSONException e) {
				createLog("U ovom JSON-u nema polja report index" + e.getMessage());
				return 0;
			}

		}
	}

	// Funkcija koja proveraba da li JSON ima sva polja koja su potrebna za
	// slot-periodic putanju
	//
	public JSONObject checkSpJSONforSend(String JSON) throws SecurityException, IOException {

		String b;
		String g;
		String j;
		String w;
		String pi;
		String po;
		String sn;
		// Uzimanje podataka iz JSON-a
		//
		String date = getParamFromJson(JSON, "date");

		JSONObject slotPeriodicBody = new JSONObject();
		JSONObject machineElement = new JSONObject();
		JSONArray machinesJSON = new JSONArray();

		// Provera da li su svi parametri tu
		//
		if (date == null) {
			slotPeriodicBody.put("error", "SP JSON koji je stigao u aplikaciju nema sve potrebne elemente za slanja");
			return slotPeriodicBody;
		}

		// Parsiranje podataka u potreban format
		//
		slotPeriodicBody.put("date", date);

		final JSONObject jsonObject = new JSONObject(JSON);
		final JSONArray machines = jsonObject.getJSONArray("machines");
		final List<JSONObject> filtederMachines = new ArrayList<JSONObject>();

		for (int i = 0; i < machines.length(); i++) {
			final JSONObject machine = machines.getJSONObject(i);
			b = machine.getString("b");
			g = machine.getString("g");
			j = machine.getString("j");
			w = machine.getString("w");
			pi = machine.getString("pi");
			po = machine.getString("po");
			sn = machine.getString("sn");

			// Sklanjanje " da bi mogli bilo koji brojevi da prodju
			//

			b = b.substring(0, b.length());
			g = g.substring(0, g.length());
			j = j.substring(0, j.length());
			w = w.substring(0, w.length());
			pi = pi.substring(0, pi.length());
			po = po.substring(0, po.length());

			machineElement = new JSONObject();
			machineElement.put("b", b);
			machineElement.put("g", g);
			machineElement.put("j", j);
			machineElement.put("w", w);
			machineElement.put("pi", pi);
			machineElement.put("po", po);
			machineElement.put("sn", sn);

			filtederMachines.add(i, machineElement);

		}

		slotPeriodicBody.put("machines", filtederMachines);

		return slotPeriodicBody;
	}

	// Funkcija koja kreira novi processing od slot-periodic koja je pronadjena u
	// bazi
	// i koja ima status 0
	//
	public void sendSlotPeriodicWithStatus0(int reportIndex, DbFunctions db, ArrayList<spProcessing> lista)
			throws SQLException, SecurityException, IOException {
		try {
			String spWithStatus0 = db.executeFunction("SELECT public.get_json_sp_by_status(0)",
					"get_json_sp_by_status");

			while (spWithStatus0 != null) {
				reportIndex = getReportIndex(spWithStatus0, "s");
				JSONObject slotPeriodicBody = checkSpJSONforSend(spWithStatus0);
				String transactionJSONError = getParamFromJson(slotPeriodicBody.toString(), "error");
				// if(transactionJSONError != null) { return; }
				db.executeProcedure("CALL public.set_sp_status_10_by_report_index(" + reportIndex + ")");
				// Pokretanje procesa za odredjeni transaction id
				spProcessing newProcess = new spProcessing(reportIndex, slotPeriodicBody);
				lista.add(newProcess);
				newProcess.start();
				spWithStatus0 = db.executeFunction("SELECT public.get_json_sp_by_status(0)", "get_json_sp_by_status");
			}
		} catch (SQLException | SecurityException | IOException e) {
			createLog(ce.sendSlotPeriodicWithStatus0 + "Greska :" + e);
		}
	}

	// Funkcija koja proverava da li ima spProcesa koji ne rade kako treba
	//
	public String getSpWorkStatus(int reportIndex, DbFunctions db) throws SecurityException, IOException {
		try {
			String workStatus = db.executeFunction("SELECT public.get_sp_report_exe_status(" + reportIndex + ")",
					"get_sp_report_exe_status");
			return workStatus;
		} catch (SQLException e) {
			createLog("work status nije kako treba" + e.getMessage());
			return "work status nije kako treba";
		}
	}

	// Funkcija za proveru cekanja do sledeceg slanja
	//
	public String getSpApiCounter(int reportIndex, DbFunctions db) throws SecurityException, IOException {
		try {
			String apiCounter = db.executeFunction("SELECT public.get_sp_api_counter(" + reportIndex + ")",
					"get_sp_api_counter");
			if (Integer.parseInt(apiCounter) < 3) {
				return "60000";
			} else {
				return "3600000";
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			createLog("Api kaunter nije kako treba" + e.getMessage());
			return "Api kaunter nije kako treba";
		}
	}

	// Cron error
	//
	public String getCronError(DbFunctions db) throws SecurityException, IOException {
		try {
			String workStatus = db.executeFunction("SELECT public.get_sp_cron_job_error_counter()",
					"get_sp_cron_job_error_counter");
			return workStatus;
		} catch (SQLException e) {
			createLog("get_sp_cron_job_error_counter nije kako treba" + e.getMessage());
			return "get_sp_cron_job_error_counter nije kako treba";
		}
	}

	// Provera vremena i zaustavljanje aplikacije
	//
	public boolean workTime() throws ParseException {

		LocalTime now = LocalTime.now();

		if (now.isAfter(LocalTime.parse("06:00:00")) && now.isBefore(LocalTime.parse("08:00:00"))) {
			return false;
		} else {
			return true;
		}

	}

	// String moze da koristi cirilicna slova
	//
	public String setUTF8(String str) {
		byte[] bytes = str.getBytes(Charsets.UTF_8);
		String strUtf8 = new String(bytes, Charsets.UTF_8);

		return strUtf8;
	}

	// String za slot periodik
	//
	public String slotPriodicCheckString(String str) {
		String msg = "";
		String jsonString = str; // assign your JSON String here
		JSONObject obj = new JSONObject(jsonString);
		String pageName = obj.getString("date");

		JSONArray arr = obj.getJSONArray("machines"); // notice that `"posts": [...]`
		for (int i = 0; i < arr.length(); i++) {
			String sn = arr.getJSONObject(i).getString("sn");
			String b  = arr.getJSONObject(i).getString("b");
			String g  = arr.getJSONObject(i).getString("g");
			String w  = arr.getJSONObject(i).getString("w");
			String pi = arr.getJSONObject(i).getString("pi");
			String j  = arr.getJSONObject(i).getString("j");
			String po = arr.getJSONObject(i).getString("po");
			msg = msg + "sn: " + sn + " b: " + b +  " g: " + g +  " w: " + w +  " pi: " + pi +  " j: " + j +  " po: " + po + "\r\n";
		}

		return msg;
	}
	


}