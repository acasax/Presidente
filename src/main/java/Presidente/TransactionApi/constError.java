package Presidente.TransactionApi;

public class constError {

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
		  default:
		    return "Nema u bazi pristiglog sid-a";
		}	
	}
	
	public int maxDeposit = 100000;
	public int maxWithdraw = 500000;
}
