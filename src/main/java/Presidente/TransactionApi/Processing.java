package Presidente.TransactionApi;

public class Processing extends Thread {
	private String 	TransactionId;
	private String  TransactionPath;
	private String  TransactionData;
	//Konsturktor osnovne klase
	public Processing (String TransactionId, String TransactionPath, String TransactionData) {
		super();
		this.TransactionId   = TransactionId;
		this.TransactionPath = TransactionPath;
		this.TransactionData = TransactionData;
	}
	
	@Override
	public void run() {
		//TODO
		
		
		System.out.println(TransactionId);
		
		
	}

}
