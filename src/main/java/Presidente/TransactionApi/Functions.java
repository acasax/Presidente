package Presidente.TransactionApi;

import java.text.DecimalFormat;

import org.json.*;

public class Functions {

	// Funkcija koja uzima iz JSON-a samo transaction_id
	//
	public String getTransansactionId(String JSON, String Status) {
		// Status s stiglo iz baze samo json
		if (Status == "s") {
			String jsonString = JSON;
			JSONObject obj = new JSONObject(jsonString);
			try {
				String transactionId = obj.getString("transaction_id");
				return transactionId;
			} catch (JSONException e) {
				e.printStackTrace();
				return "U ovom JSON-u nema polja transaction_id";
			}
			
		} else {
			String str = JSON.substring(JSON.indexOf("{"));
			String jsonString = str;
			JSONObject obj = new JSONObject(jsonString);
			String transactionId = obj.getString("transaction_id");
			return transactionId;
		}
	}

	// Funkcija koja uzima iz JSON-a samo path
	//
	public String getTransansactionPath(String JSON, String Status) {
		// Status s stiglo iz baze samo json
		if (Status == "s") {
			String jsonString = JSON;
			JSONObject obj = new JSONObject(jsonString);
			try {
				String transactionPath = obj.getString("path");
				return transactionPath;
			} catch (JSONException e) {
				e.printStackTrace();
				return "U ovom JSON-u nema polja path";
			}
			
		} else {
			String str = JSON.substring(JSON.indexOf("{"));
			String jsonString = str;
			JSONObject obj = new JSONObject(jsonString);
			String transactionPath = obj.getString("path");
			return transactionPath;
		}
	}

	// Funkcija koja uzima odredjeni parametar iz JSON-a
	//
	public String getParamFromJson(String JSON, String Param) {
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
				e.printStackTrace();
				return null;
			}
		case "transaction_withdraw_amount":
			try {
				paramValueD = obj.getDouble(Param);
				return String.valueOf(f.format(paramValueD));
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		default:
			try {
				paramValue = obj.getString(Param);
				return paramValue;
			} catch (JSONException e) {
				//e.printStackTrace();
				return null;
			}
		}

	}

	// Funkcija koja proveraba da li JSON ima sva polja koja su potrebna za
	// odredjenu putanju
	//
	public JSONObject checkJSONforSend(String JSON, String path) {

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

	// Funkcija koja iz provere salje zahteve na transakcioni Api
	//
	public void sendTransactionToApi() {

	}

}
