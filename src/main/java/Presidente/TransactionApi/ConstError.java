package Presidente.TransactionApi;

public class ConstError {

	public String sendTransactionWithStatus0   = "Funkcija sendTransactionWithStatus0 nije uspešno izvršena";
	public String sendTransaction              = "Funkcija sendTransaction nije uspešno izvršena";
	public String asyconnect                   = "Funkcija asyconnect nije uspešno izvršena";
	public String executeQuery                 = "Funkcija executeQuery nije uspešno izvršena";
	public String executeProcedure             = "Funkcija executeProcedure nije uspešno izvršena";
	public String executeFunction              = "Funkcija executeFunction nije uspešno izvršena";
	public String executeQuery1                = "Funkcija executeQuery1 nije uspešno izvršena";
	public String getParamFromJsonDuable       = "Parametar koji ste zatrazili nije dabl formata";
	public String getParamFromJson             = "U jsonu nema unetog parametra";
	public String sendSlotPeriodicWithStatus0  = "Funkcija sendSlotPeriodicWithStatus0 nije uspešno izvršena";	
	
	
	public String slotClubIdFromSlotClubSid(String sid) {
		switch(sid) {
		  case "127000008":
		    return "01";
		  case "127000002":
			    return "02";
		  case "127000017":
			    return "03";
		  case "127000004":
			    return "04";
		  case "127000007":
			    return "07";
		  case "1270000010":
			    return "08";
		  case "127000012":
			    return "09";
		  case "127000013":
			    return "10";
		  case "127000014":
			    return "11";
		  case "127000015":
			    return "12";
		  case "127000016":
			    return "13";
		  case "127000018":
			    return "14";
		  default:
		    return "Nema u bazi pristiglog sid-a";
		}	
	}
	
	public Integer transactionTybeByString(String type, String path) {
		if(type.equals("deposit") && path.equals("slot/deposit")) {
			return 1;
		}
		if(type.equals("witdraw") && path.equals("slot/withdraw")) {
			return 2;
		}
		if(type.equals("jackpot") && path.equals("slot/jackpot")) {
			return 3;
		}
		if(type.equals("rollback") && path.equals("slot/rollback")) {
			return 4;
		}
		if(type.equals("deposit") && path.equals("slot/deposit")) {
			return 5;
		}
		if(type.equals("deposit_bills") && path.equals("slot/deposit")) {
			return 6;
		}
		if(type.equals("deposit_ticket_in") && path.equals("slot/deposit")) {
			return 7;
		}
		if(type.equals("deposit") && path.equals("slot/deposit")) {
			return 8;
		}
		return 0;
	}
	
	public Double maxDeposit = 100000.00;
	public Double maxWithdraw = 500000.00;
}
