package Presidente.TransactionApi;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import java.util.logging.FileHandler;
import java.util.logging.Logger;

import org.json.*;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.io.File;
import java.io.FilenameFilter;

public class Functions {

	// Funkcija koja uzima iz JSON-a samo transaction_id
	//
	public String getTransansactionId(String JSON, String Status) throws SecurityException, IOException {
		// Status s stiglo iz baze samo json
		if (Status == "s") {
			String jsonString = JSON;

			try {
				JSONObject obj = new JSONObject(jsonString);
				String transactionId = obj.getString("transaction_id");
				return transactionId;
			} catch (JSONException e) {
				createLog("U ovom JSON-u nema polja transaction_id" + e.getMessage());
				return "U ovom JSON-u nema polja transaction_id";
			}

		} else {
			try {
				String str = JSON.substring(JSON.indexOf("{"));
				String jsonString = str;
				JSONObject obj = new JSONObject(jsonString);
				String transactionId = obj.getString("transaction_id");
				return transactionId;
			} catch (JSONException e) {
				createLog("U ovom JSON-u nema polja transaction_id" + e.getMessage());
				return "U ovom JSON-u nema polja transaction_id";
			}

		}
	}

	// Funkcija koja uzima iz JSON-a samo path
	//
	public String getTransansactionPath(String JSON, String Status) throws SecurityException, IOException {
		// Status s stiglo iz baze samo json
		if (Status == "s") {
			String jsonString = JSON;
			try {
				JSONObject obj = new JSONObject(jsonString);
				String transactionPath = obj.getString("path");
				return transactionPath;
			} catch (JSONException e) {
				createLog("U ovom JSON-u nema polja path" + e.getMessage());
				return "U ovom JSON-u nema polja path";
			}

		} else {
			try {
				String str = JSON.substring(JSON.indexOf("{"));
				String jsonString = str;
				JSONObject obj = new JSONObject(jsonString);
				String transactionPath = obj.getString("path");
				return transactionPath;
			} catch (JSONException e) {
				createLog("U ovom JSON-u nema polja path" + e.getMessage());
				return "U ovom JSON-u nema polja path";
			}

		}
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
				paramValueD = obj.getDouble(Param);
				return String.valueOf(f.format(paramValueD));
			} catch (JSONException e) {
				createLog(e.getMessage());
				return null;
			}
		case "transaction_withdraw_amount":
			try {
				paramValueD = obj.getDouble(Param);
				return String.valueOf(f.format(paramValueD));
			} catch (JSONException e) {
				createLog(e.getMessage());
				return null;
			}
		default:
			try {
				paramValue = obj.getString(Param);
				return paramValue;
			} catch (JSONException e) {
				if (!Param.equals("error")) {
					createLog(e.getMessage());
				}
				return null;
			}
		}

	}

	// Funkcija koja proveraba da li JSON ima sva polja koja su potrebna za
	// odredjenu putanju
	//
	public JSONObject checkJSONforSend(String JSON, String path) throws SecurityException, IOException {

		// Uzimanje podataka iz JSON-a
		//
		String transaction_time = getParamFromJson(JSON, "transaction_time");
		String transaction_id = getParamFromJson(JSON, "transaction_id");
		String transaction_amount = getParamFromJson(JSON, "transaction_amount");
		String transaction_type = getParamFromJson(JSON, "transaction_type");
		String slot_club_id = getParamFromJson(JSON, "slot_club_id");
		String sticker_no = getParamFromJson(JSON, "sticker_number");

		JSONObject transactionBody = new JSONObject();

		// Provera da li su svi parametri tu
		//
		if (transaction_time == null || transaction_id == null || transaction_amount == null || transaction_type == null
				|| slot_club_id == null || sticker_no == null) {
			transactionBody.put("error", "JSON koji je stigao u aplikaciju nema sve potrebne elemente za slanja");
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
			return transactionBody;
		case "slot/withdraw":
			transactionBody.put("transaction_time", transaction_time);
			transactionBody.put("transaction_id", transaction_id);
			transactionBody.put("transaction_amount", p_transaction_amount);
			transactionBody.put("transaction_type", transaction_type);
			transactionBody.put("slot_club_id", slot_club_id);
			transactionBody.put("sticker_no", sticker_no);
			return transactionBody;
		case "slot/jackpot":
			// Ovde je zato sto postoji samo za ovu rutu
			//
			//String transaction_withdraw_amount = getParamFromJson(JSON, "transaction_withdraw_amount");
			//Double p_transaction_withdraw_amount = Double.valueOf(transaction_withdraw_amount); // Konvertovanje u
																								// potrebni tip

			transactionBody.put("transaction_time", transaction_time);
			transactionBody.put("transaction_id", transaction_id);
			transactionBody.put("transaction_amount", p_transaction_amount);
			transactionBody.put("transaction_type", transaction_type);
			transactionBody.put("slot_club_id", slot_club_id);
			transactionBody.put("sticker_no", sticker_no);
			transactionBody.put("transaction_withdraw_amount", 0);
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
			//transactionBody.put("rollback_transaction_id", rollback_transaction_id);
			return transactionBody;
		/*
		 * case "imports/slot-periodic": break; case "casino": break;
		 */
		default:
			transactionBody.put("error", "Putanja koju ste poslali u funkciju nije dobra");
			return transactionBody;
		}
	}

	// Funkcija za proveru cekanja do sledeceg slanja
	//
	public String getApiCounter(String transaction_id, DbFunctions db, Connection con)
			throws SecurityException, IOException {
		try {
			String apiCounter = db.executeFunction("SELECT public.get_api_counter('" + transaction_id + "')", con,
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

	public String getWorkStatus(String transaction_id, DbFunctions db, Connection con)
			throws SecurityException, IOException {
		try {
			String workStatus = db.executeFunction("SELECT public.get_transaction_exe_status('" + transaction_id + "')",
					con, "get_transaction_exe_status");
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
	      //Creating the directory
	     file.mkdir();
		DateFormat FileNameFormat = new SimpleDateFormat("dd-M-yyyy_hh-mm-ss");
		Date FileName = new Date();
		FileHandler handler = new FileHandler("logs/" + "log_" + FileNameFormat.format(FileName) + ".log");

		Logger logger = Logger.getLogger("ResivoJe");
		logger.addHandler(handler);

		logger.warning(msg);
		

	}

	// Funkcija za slanje e-mail
	//
	public void sendEmail(String msg) {
		// Recipient's email ID needs to be mentioned.
		String to = "resivojee@gmail.com";

		// Sender's email ID needs to be mentioned
		String from = "prezidentplay1@gmail.com";

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

				return new PasswordAuthentication("prezidentplay1@gmail.com", "igrajdabidobio");

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
			message.setSubject("Postoji greska na sistemu");

			// Now set the actual message
			message.setText(msg);

			Transport.send(message);
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}

	}
	 
	    //Funkcija za slanje e-mail Saletu
		//
		public void sendEmailYahho(String msg) {
			// Recipient's email ID needs to be mentioned.
			String to = "miladinovicsasa@yahoo.com";

			// Sender's email ID needs to be mentioned
			String from = "prezidentplay1@gmail.com";

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

					return new PasswordAuthentication("prezidentplay1@gmail.com", "igrajdabidobio");

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
				message.setSubject("Postoji cron greska na sistemu Trend Play");

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
			sendEmail(msg);
		}

		if (flag == 0) {
			msg = "Nema pronadjenih log fajlova";
			sendEmail(msg);
		}

	}

	// Funkcija koja uzima iz JSON-a samo report index
	//
	public int getReportIndex(String JSON, String Status) throws SecurityException, IOException {
		// Status s stiglo iz baze samo json
		if (Status == "s") {
			String jsonString = JSON;
			int report_index = 0;
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
				JSONObject obj = new JSONObject(jsonString);
				String transactionId = obj.getString("transaction_id");
				return 1;
			} catch (JSONException e) {
				createLog("U ovom JSON-u nema polja transaction_id" + e.getMessage());
				return 0;
			}

		}
	}

	// Funkcija koja proveraba da li JSON ima sva polja koja su potrebna za
	// slot-periodic putanju
	//
	public JSONObject checkSpJSONforSend(String JSON) throws SecurityException, IOException {

		int b;
		int g;
		int j;
		int w;
		int pi;
		int po;
		String sn;
		// Uzimanje podataka iz JSON-a
		//
		String date = getParamFromJson(JSON, "date");
	

		JSONObject slotPeriodicBody = new JSONObject();
		JSONObject machineElement   = new JSONObject();
		JSONArray  machinesJSON   = new JSONArray();
		
		
		
		// Provera da li su svi parametri tu
		//
		if (date == null ) {
			slotPeriodicBody.put("error", "SP JSON koji je stigao u aplikaciju nema sve potrebne elemente za slanja");
			return slotPeriodicBody;
		}

		// Parsiranje podataka u potreban format
		//
        slotPeriodicBody.put("date", date);
		
        final JSONObject jsonObject = new JSONObject(JSON);
		final JSONArray machines = jsonObject.getJSONArray("machines");
		for (int i = 0; i < machines.length(); i++) {
			final JSONObject machine = machines.getJSONObject(i);
			b  = machine.getInt("b");
			g  = machine.getInt("g");
			j  = machine.getInt("j");
			w  = machine.getInt("w");
			pi = machine.getInt("pi");
			po = machine.getInt("po");
			sn = machine.getString("sn");
			
			machineElement.put("b", b);
			machineElement.put("g", g);
			machineElement.put("j", j);
			machineElement.put("w", w);
			machineElement.put("pi", pi);
			machineElement.put("po", po);
			machineElement.put("sn", sn);
		
			machinesJSON.put(machineElement);
		}
			
		slotPeriodicBody.put("machines", machinesJSON);
		
		return slotPeriodicBody;
	}

	// Funkcija koja kreira novi processing od slot-periodic koja je pronadjena u
	// bazi
	// i koja ima status 0
	//
	public void sendSlotPeriodicWithStatus0(Connection lConn, int reportIndex, DbFunctions db,
			ArrayList<spProcessing> lista) throws SQLException, SecurityException, IOException {
		try {
			String spWithStatus0 = db.executeFunction("SELECT public.get_json_sp_by_status(0)", lConn,
					"get_json_sp_by_status");

			while (spWithStatus0 != null) {
				reportIndex = getReportIndex(spWithStatus0, "s");
				JSONObject slotPeriodicBody = checkSpJSONforSend(spWithStatus0);
				String slotPeriodicJSONError = getParamFromJson(slotPeriodicBody.toString(), "error");
				if (slotPeriodicJSONError != null) {
					createLog(slotPeriodicJSONError); // kreira log fajl sa greskom o parametrima
				} else {
					// Procedura Set Status 10
					db.executeProcedure("CALL public.set_sp_status_10_by_report_index(" + reportIndex + ")", lConn);
					// Pokretanje procesa za odredjeni transaction id
					spProcessing newProcess = new spProcessing(reportIndex, slotPeriodicBody, lConn);
					lista.add(newProcess);
					newProcess.start();
					spWithStatus0 = db.executeFunction("SELECT public.get_json_sp_by_status(0)", lConn,
							"get_json_sp_by_status");
				}
			}
		} catch (SQLException | SecurityException | IOException e) {
			createLog(e.getMessage());
		}
	}
	
	//Funkcija koja proverava da li ima spProcesa koji ne rade kako treba
	//
	public String getSpWorkStatus(int reportIndex, DbFunctions db, Connection con)
			throws SecurityException, IOException {
		try {
			String workStatus = db.executeFunction("SELECT public.get_sp_report_exe_status(" + reportIndex + ")",
					con, "get_sp_report_exe_status");
			return workStatus;
		} catch (SQLException e) {
			createLog("work status nije kako treba" + e.getMessage());
			return "work status nije kako treba";
		}
	}
	
	// Funkcija za proveru cekanja do sledeceg slanja
		//
		public String getSpApiCounter(int reportIndex, DbFunctions db, Connection con)
				throws SecurityException, IOException {
			try {
				String apiCounter = db.executeFunction("SELECT public.get_sp_api_counter(" + reportIndex + ")", con,
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
		
		//Cron error 
		public String getCronError( DbFunctions db, Connection con)
				throws SecurityException, IOException {
			try {
				String workStatus = db.executeFunction("SELECT public.get_sp_cron_job_error_counter()",
						con, "get_sp_cron_job_error_counter");
				return workStatus;
			} catch (SQLException e) {
				createLog("get_sp_cron_job_error_counter nije kako treba" + e.getMessage());
				return "get_sp_cron_job_error_counter nije kako treba";
			}
		}

}
