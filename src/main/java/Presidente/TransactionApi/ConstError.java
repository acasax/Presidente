package Presidente.TransactionApi;

public class ConstError {

	public String sendTransactionWithStatus0   = "Funkcija sendTransactionWithStatus0 nije uspešno izvršena";
	public String sendTransaction              = "Funkcija sendTransaction nije uspešno izvršena";
	public String asyconnect                   = "Funkcija asyconnect nije uspešno izvršena";
	public String executeQuery                 = "Funkcija executeQuery nije uspešno izvršena";
	public String executeProcedure             = "Funkcija executeProcedure nije uspešno izvršena";
	public String executeFunction              = "Funkcija executeFunction nije uspešno izvršena";
	public String executeQuery1                = "Funkcija executeQuery1 nije uspešno izvršena";
	public String executeQuery2                = "Funkcija executeQuery2 nije uspešno izvršena";
	public String executeQuery3                = "Funkcija executeQuery3 nije uspešno izvršena";
	public String executeQuery4                = "Funkcija executeQuery4 nije uspešno izvršena";
	public String getParamFromJsonDuable       = "Parametar koji ste zatrazili nije dabl formata";
	public String getParamFromJson             = "U jsonu nema unetog parametra";
	public String sendSlotPeriodicWithStatus0  = "Funkcija sendSlotPeriodicWithStatus0 nije uspešno izvršena";	
	
	
	public String slotClubFromSlotClubid(String sid) {
		switch(sid) {
		  case "01":
		    return "Trg Despota Stefana 30 Krusevac";
		  case "02":
			    return "Cara Lazara 193 Krusevac";
		  case "03":
			    return "29. Novembra Aleksandrovac";
		  case "04":
			    return "Kralja Petra i 42 Brus";
		  case "07":
			    return "Vojvode Misica 8 Paracin";
		  case "08":
			    return "Bircaninova 10 Krusevac";
		  case "09":
			    return "Vidovdanska 233 Krusevac";
		  case "10":
			    return "Dimitrija Tucakovica 40 Kraljevo";
		  case "11":
			    return "Rasinska 101 Krusevac";
		  case "12":
			    return "Zrenjaninski put 155 Borca";
		  case "13":
			    return "Trg Kralja Petra I Oslobodioca 3/1 Kraljevo";
		  case "14":
			    return "Čolak Antina 17 Kruševac";
		  default:
		    return "Nema u bazi pristiglog sid-a";
		}	
	}
	
	public String transactionTypeByPath(String sid) {
		switch(sid) {
		  case "slot/deposit":
		    return "uplata";
		  case "slot/withdraw":
			    return "isplata";
		  case "slot/jackpot":
			    return "dzekpot";
		  case "slot/rollback":
			    return "storno";
		  default:
		    return "Nema u bazi pristiglog sid-a";
		}	
	}
	
	
	
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
		  case "0127000008":
			    return "01";
		  case "0127000002":
				return "02";
		  case "0127000017":
				return "03";
		  case "0127000004":
				return "04";
		  case "0127000007":
				return "07";
	      case "01270000010":
				return "08";
		  case "0127000012":
				return "09";
	      case "0127000013":
				return "10";
	      case "0127000014":
				return "11";
	      case "0127000015":
				return "12";
		  case "0127000016":
				return "13";
		  case "0127000018":
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
	
	public String transactionPathByType(Integer type) {
		switch(type) {
		  case 1:
		    return "slot/deposit";
		  case 2:
			    return "slot/withdraw";
		  case 3:
			    return "slot/jackpot";
		  case 4:
			    return "slot/rollback";
		  case 5:
			    return "slot/deposit";
		  case 6:
			    return "slot/deposit";
		  case 7:
			    return "slot/deposit";
		  case 8:
			    return "slot/withdraw";
		  default:
		    return "Nema u bazi pristiglog tipa";
		}	
	}
	
	public Double maxDeposit = 500000.00;
	public Double maxWithdraw = 200000.00;
}
