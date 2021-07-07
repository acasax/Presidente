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

import java.util.Date;
import java.util.Properties;
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

public class Functions {

	// Funkcija koja uzima iz JSON-a samo transaction_id
	//
	public String getTransansactionId(String JSON, String Status) throws SecurityException, IOException  {
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
	public String getTransansactionPath(String JSON, String Status) throws SecurityException, IOException  {
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
				if(!Param.equals("error")) {
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
		String sticker_no = getParamFromJson(JSON, "sticker_no");

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
			String transaction_withdraw_amount = getParamFromJson(JSON, "transaction_withdraw_amount");
			Double p_transaction_withdraw_amount = Double.valueOf(transaction_withdraw_amount); // Konvertovanje u
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
			transactionBody.put("rollback_transaction_id", rollback_transaction_id);
			return transactionBody;
		/*
		 * case "imports/slot-periodic": break; case "casino": break;
		 */
		default:
			transactionBody.put("error", "Putanja koju ste poslali u funkciju nije dobra");
			return transactionBody;
		}
	}

	//Funkcija za proveru cekanja do sledeceg slanja
	//
	public String getApiCounter(String transaction_id, DbFunctions db, Connection con) throws SecurityException, IOException {
		try {
			String apiCounter = db.executeFunction("SELECT public.get_api_counter('" + transaction_id + "')", con, "get_api_counter");
			if(Integer.parseInt(apiCounter) < 3) {
				return "60000";
			}else {
				return "3600000";
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			createLog("Api kaunter nije kako treba" + e.getMessage());
			return "Api kaunter nije kako treba";
		}
	}
	
	public String getWorkStatus(String transaction_id, DbFunctions db, Connection con) throws SecurityException, IOException {
		try {
			String workStatus = db.executeFunction("SELECT public.get_transaction_exe_status('" + transaction_id + "')", con, "get_transaction_exe_status");
			return workStatus;
		} catch (SQLException e) {
			createLog("work status nije kako treba" + e.getMessage());
			return "work status nije kako treba";
		}
	}
	
	//Funkcija koja kreira log fajlove sa greskom
	//
	public void createLog(String msg) throws SecurityException, IOException {
			DateFormat FileNameFormat = new SimpleDateFormat("dd-M-yyyy_hh-mm-ss");
			Date FileName = new Date();
	        FileHandler handler = new FileHandler("log_" + FileNameFormat.format(FileName) + ".log");
	 
	        Logger logger = Logger.getLogger("ResivoJe");
	        logger.addHandler(handler);
	         
	        logger.warning(msg);
	}

	//Funkcija za slanje e-mail
	//
	public void sendEmail() 
	{
		 // Recipient's email ID needs to be mentioned.
        String to = "resivojee@gmail.com";

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

                return new PasswordAuthentication("acasax@gmail.com", "Podlogazamis123");

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
            message.setSubject("Poslato iz Jave napokon");

            // Now set the actual message
            message.setText("Da mi pusis kurac vise ");

            System.out.println("sending...");
            // Send message
            Transport.send(message);
            System.out.println("Sent message successfully....");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        } 
		
		
	}
}
