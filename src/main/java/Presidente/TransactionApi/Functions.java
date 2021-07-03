package Presidente.TransactionApi;
import org.json.*;

public class Functions {

	public String getTransansactionId(String JSON) {
		String jsonString = JSON; //assign your JSON String here
		JSONObject obj = new JSONObject(jsonString);
		String transactionId = obj.getString("transaction_id");
		return transactionId;
	}
}
