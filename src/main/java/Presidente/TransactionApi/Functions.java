package Presidente.TransactionApi;
import org.json.*;

public class Functions {

	public String getTransansactionId(String JSON, String Status) {
		if(Status == "s") {
			String jsonString = JSON; //assign your JSON String here
			JSONObject obj = new JSONObject(jsonString);
			String transactionId = obj.getString("transaction_id");
			return transactionId;
		}else {
			 //assign your JSON String here
			 String str = JSON.substring(JSON.indexOf("{"));
			String jsonString = str;
			JSONObject obj = new JSONObject(jsonString);
			String transactionId = obj.getString("transaction_id");
			return transactionId;
		}
	}
	
	public String getTransansactionPath(String JSON, String Status) {
		if(Status == "s") {
			String jsonString = JSON; //assign your JSON String here
			JSONObject obj = new JSONObject(jsonString);
			String transactionPath = obj.getString("path");
			return transactionPath;
		}else {
			 //assign your JSON String here
			 String str = JSON.substring(JSON.indexOf("{"));
			String jsonString = str;
			JSONObject obj = new JSONObject(jsonString);
			String transactionPath = obj.getString("path");
			return transactionPath;
		}
	}
}
