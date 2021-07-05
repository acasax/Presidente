package Presidente.TransactionApi;

import org.json.JSONObject;

public class Processing extends Thread {
	private String 	TransactionId;
	private String  TransactionPath;
	private JSONObject  TransactionBody;
	//Konsturktor osnovne klase
	public Processing (String TransactionId, String TransactionPath, JSONObject TransactionBody) {
		super();
		this.TransactionId   = TransactionId;
		this.TransactionPath = TransactionPath;
		this.TransactionBody = TransactionBody;
	}
	
	@Override
	public void run() {
		//TODO
		
		
		System.out.println(TransactionId + TransactionPath + TransactionBody);
		
		try {
			Thread.sleep(6000000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
