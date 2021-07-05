package Presidente.TransactionApi;
import java.text.DecimalFormat;

import org.json.*;

public class Functions {

	//Funkcija koja uzima iz JSON-a samo transaction_id
	//
	public String getTransansactionId(String JSON, String Status) {
		//Status s stiglo iz baze samo json
		if(Status == "s") {
			String jsonString = JSON;
			JSONObject obj = new JSONObject(jsonString);
			String transactionId = obj.getString("transaction_id");
			return transactionId;
		}else {
			String str = JSON.substring(JSON.indexOf("{"));
			String jsonString = str;
			JSONObject obj = new JSONObject(jsonString);
			String transactionId = obj.getString("transaction_id");
			return transactionId;
		}
	}
	
	//Funkcija koja uzima iz JSON-a samo path
	//
	public String getTransansactionPath(String JSON, String Status) {
		//Status s stiglo iz baze samo json
		if(Status == "s") {
			String jsonString = JSON; 
			JSONObject obj = new JSONObject(jsonString);
			String transactionPath = obj.getString("path");
			return transactionPath;
		}else {
			String str = JSON.substring(JSON.indexOf("{"));
			String jsonString = str;
			JSONObject obj = new JSONObject(jsonString);
			String transactionPath = obj.getString("path");
			return transactionPath;
		}
	}
	
	//Funkcija koja uzima odredjeni parametar iz JSON-a
	//
	public String getParamFromJson(String JSON, String Param) {
		String jsonString = JSON;
		double paramValueD;
		String paramValue;
		DecimalFormat f = new DecimalFormat("##.##");
		JSONObject obj = new JSONObject(jsonString);
		switch(Param){
		case "transaction_amount":
			paramValueD = obj.getDouble(Param);
			return String.valueOf(f.format(paramValueD));
		case "transaction_withdraw_amount":
			paramValueD = obj.getDouble(Param);
			return String.valueOf(f.format(paramValueD)); 
		default:
			paramValue = obj.getString(Param);
			return paramValue;
		}
		
	}
	
	//Funkcija koja proveraba da li JSON ima sva polja koja su potrebna za odredjenu putanju
	//
	public JSONObject checkJSONforSend(String JSON, String path) {
		//Uzimanje podataka iz JSON-a
		//
		String transaction_time             = getParamFromJson(JSON, "transaction_time");
		String transaction_id               = getParamFromJson(JSON, "transaction_id");
		String transaction_amount           = getParamFromJson(JSON, "transaction_amount");
		String transaction_type             = getParamFromJson(JSON, "transaction_type");
		String slot_club_id                 = getParamFromJson(JSON, "slot_club_id");
		String sticker_no                   = getParamFromJson(JSON, "sticker_no");
		
		
		//Parsiranje podataka u potreban format 
		//
		Double p_transaction_amount = Double.valueOf(transaction_amount);
		
		JSONObject transactionBody = new JSONObject();
		
		switch(path){
		case "imports/slot/deposit":
			transactionBody.put("transaction_time", transaction_time);
			transactionBody.put("transaction_id", transaction_id);
			transactionBody.put("transaction_amount", p_transaction_amount);
			transactionBody.put("transaction_type", transaction_type);
			transactionBody.put("slot_club_id", slot_club_id);
			transactionBody.put("sticker_no", sticker_no);
			return transactionBody;
		case "imports/slot/withdraw":
			transactionBody.put("transaction_time", transaction_time);
			transactionBody.put("transaction_id", transaction_id);
			transactionBody.put("transaction_amount", p_transaction_amount);
			transactionBody.put("transaction_type", transaction_type);
			transactionBody.put("slot_club_id", slot_club_id);
			transactionBody.put("sticker_no", sticker_no);
			return transactionBody;
		case "imports/slot/jackpot":
			//Ovde je zato sto postoji samo za ovu rutu
			//
			String transaction_withdraw_amount  = getParamFromJson(JSON, "transaction_withdraw_amount");
			Double p_transaction_withdraw_amount = Double.valueOf(transaction_withdraw_amount); // Konvertovanje u potrebni tip
			
			transactionBody.put("transaction_time", transaction_time);
			transactionBody.put("transaction_id", transaction_id);
			transactionBody.put("transaction_amount", p_transaction_amount);
			transactionBody.put("transaction_type", transaction_type);
			transactionBody.put("slot_club_id", slot_club_id);
			transactionBody.put("sticker_no", sticker_no);
			transactionBody.put("transaction_withdraw_amount", p_transaction_withdraw_amount);
			return transactionBody;
		case "imports/slot/rollback":
			//Ovde je zato sto postoji samo za ovu rutu
			//
			String rollback_transaction_id       = getParamFromJson(JSON, "rollback_transaction_id");
			transactionBody.put("transaction_time", transaction_time);
			transactionBody.put("transaction_id", transaction_id);
			transactionBody.put("transaction_amount", p_transaction_amount);
			transactionBody.put("transaction_type", transaction_type);
			transactionBody.put("slot_club_id", slot_club_id);
			transactionBody.put("sticker_no", sticker_no);
			transactionBody.put("rollback_transaction_id", rollback_transaction_id);
			return transactionBody;
		/*case "imports/slot-periodic":
			break;
		case "casino":
			break;*/
		default:
			return null;
		}
	}
}
