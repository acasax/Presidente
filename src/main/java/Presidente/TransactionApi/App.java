package Presidente.TransactionApi;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.concurrent.ExecutionException;

import org.json.JSONObject;

import java.util.ArrayList;
import java.time.LocalDateTime;

public class App {
	static Listener listener = null;
	static ArrayList<Processing> lista = new ArrayList<>();
	static String notifyTransaction;

	static Object pgconn;
	static String transactionWithStatus0;
	static String transactionId;
	static String transactionPath;
	static String transactionWithtransactionId;
	static JSONObject transactionBody;
	static String transactionJSONError;
	static String transactionSendingStatus;

	static DbFunctions db = new DbFunctions();
	static Functions fun = new Functions();
	static ConstError ce = new ConstError();

	// Ne radi jer je uvek lista prazna 
	// build version 48
	// Da li postoji proces sa zadatim transaction_id koji radi
	public static Processing nadjiProcessing(String transactionId) {
		System.out.println("App nadjiProcessing: " + transactionId);
		System.out.println("App lista.size(): " + lista.size());
		for (int i = 0; i < lista.size(); i++) {
			if (lista.get(i).getTransactionId().equals(transactionId)) {
				Processing badProcess = lista.get(i);
				System.out.println("App badProcess: " + badProcess);
				lista.remove(i);
				return badProcess;
			}
		}
		return null;
	}

	// Prekida proces
	public static void prekini(Processing p) {
		p.interrupt();
	}

	// Funkcija koja kreira novi processing od transakcije koja je stigla iz notifya
	//
	public static void sendTransaction(String transaction, Connection lConn)
			throws SecurityException, IOException, SQLException, ParseException {
		if (fun.workTime()) {
			try {
				if (transaction != null) {
					transactionId = fun.getTransansactionId(transaction, "s");
					transactionPath = fun.getTransansactionPath(transaction, db, false);
					transactionBody = fun.checkJSONforSend(transaction, transactionPath, db, false, lConn);
					transactionJSONError = fun.getParamFromJson(transactionBody.toString(), "error");
					transactionSendingStatus = fun.getParamFromJson(transactionBody.toString(), "send_status");
					if (transactionJSONError != null || transactionSendingStatus != null) {
						if (transactionJSONError != null) {
							fun.createLog("sendTransaction transactionJSONError problem sa vremenom transakcije: " + transactionJSONError);
							fun.sendEmail(transactionJSONError, "presidenteapp@yahoo.com", "Probelm sa vremenom transakcije");
						} else {
							fun.createLog("sendTransaction transactionSendingStatus: " + transactionSendingStatus);
						}
					} else {
						System.out.println("sendTransaction before Threads number" + ManagementFactory.getThreadMXBean().getThreadCount());
						//if(ManagementFactory.getThreadMXBean().getThreadCount() < 200) {
							System.out.println("sendTransaction number of Threads" + ManagementFactory.getThreadMXBean().getThreadCount());
							// Procedura Set Status 10
							db.executeProcedure("CALL public.set_status_10_by_transaction_id('" + transactionId + "')", lConn);
							// Pokretanje procesa za odredjeni transaction id
							Processing newProcess = new Processing(transactionId, transactionPath, transactionBody, lConn);
							lista.add(newProcess);
							newProcess.start();
						//}
					}
				}
			} catch (SecurityException | IOException e) {
				fun.createLog("sendTransaction SQLException | SecurityException | IOException e" +
						ce.sendTransactionWithStatus0 + " Transaction id: " + transactionId + "  Transaction path: "
								+ transactionPath + "Transaction body : " + transactionBody + "Greska :" + e);
			}
		}
	}

	// Funkcija koja kreira novi processing od transakcija koja je pronadjena u bazi
	// i koja ima status 0
	//
	
	public static void sendTransactionWithStatus0(Connection lConn)
			throws SQLException, SecurityException, IOException, ParseException {
		System.out.println("sendTransactionWithStatus0 start");
		if (fun.workTime()) {
			try {
				transactionWithStatus0 = db.executeFunction("SELECT public.get_json_by_status(0)",
						"get_json_by_status", lConn);
				System.out.println("sendTransactionWithStatus0 start");
				while (transactionWithStatus0 != null) {
					transactionId = fun.getTransansactionId(transactionWithStatus0, "s");
					transactionPath = fun.getTransansactionPath(transactionWithStatus0, db, true);
					transactionBody = fun.checkJSONforSend(transactionWithStatus0, transactionPath, db, true, lConn);
					transactionSendingStatus = fun.getParamFromJson(transactionBody.toString(), "send_status");
					transactionJSONError = fun.getParamFromJson(transactionBody.toString(), "error");
					if (transactionJSONError != null || transactionSendingStatus != null) {
						if (transactionJSONError != null) {
							fun.createLog("sendTransactionWithStatus0 transactionJSONError problem sa vremenom transakcije: " + transactionJSONError);
							fun.sendEmail(transactionJSONError, "presidenteapp@yahoo.com", "Probelm sa vremenom transakcije");
						} else {
							fun.createLog("sendTransactionWithStatus0 transactionSendingStatus: " + transactionSendingStatus);
						}
					} else {
						System.out.println("Pre 0 " + ManagementFactory.getThreadMXBean().getThreadCount());
						if(ManagementFactory.getThreadMXBean().getThreadCount() < 6500) {
							System.out.println("Total Number of threads " + ManagementFactory.getThreadMXBean().getThreadCount());
							// Procedura Set Status 10
							db.executeProcedure("CALL public.set_status_10_by_transaction_id('" + transactionId + "')", lConn);
							// Pokretanje procesa za odredjeni transaction id
							Processing newProcess = new Processing(transactionId, transactionPath, transactionBody, lConn);
							lista.add(newProcess);
							newProcess.start();
							transactionWithStatus0 = db.executeFunction(sqlConsts.sqlGetJsonWithStatus0,
									sqlConsts.columnsGetJsonWithStatus[0], lConn);
						}
					}
				}
			} catch (SQLException | SecurityException | IOException e) {
				fun.createLog("sendTransactionWithStatus0 SQLException | SecurityException | IOException e" +
						ce.sendTransactionWithStatus0 + " Transaction id: " + transactionId + "  Transaction path: "
								+ transactionPath + "Transaction body : " + transactionBody + "Greska :" + e);
			}
		}
	}

	public static void main(String[] args) throws SQLException, InterruptedException, ExecutionException,
			SecurityException, IOException, ParseException {

		
		/*
		 * updateMachines um = new updateMachines();
		 * 
		 * um.start();
		 */
		 

		/*
		 * stickerNumberFromExel snfx = new stickerNumberFromExel(); snfx.start();
		 */
		
		  Connection lConn = db.asyconnect();
		  
		  slotPeriodicCheck spc = new slotPeriodicCheck(lConn); 
		  spStart sp = new spStart(lConn);
		  Check ck = new Check(lConn);
		  ErrorCheck ec = new ErrorCheck();
		  locationCheck lc = new locationCheck(lConn); 
		  shitsHapend sh = new shitsHapend(lConn);
		  spErrorCheck spec = new spErrorCheck(lConn);
		  paymentCheck pc = new paymentCheck(lConn);
		  apiUuidStatus aus = new apiUuidStatus(lConn);
		  badTransactions bt = new badTransactions(lConn);
		  checkCertificate cc = new checkCertificate();
		  
		  // Proverava sertifikate
		  // 
		  cc.start();
		  
		  
		  // Email za proveru aplikacije 
		  //
		  fun.sendEmail("Aplikacija se startovala u: " + LocalDateTime.now(),
				  "presidenteapp@yahoo.com", "Pokretanje aplikacije");
		  
		  // Proverava da li slanje ka upravi radi 
		  // 
		  aus.start();
		  
		  // Proverava da li ima log fajlova 
		  //
		  ec.start();
		  
		  // Provera lokacija u poslednja dva sata 
		  // 
		  lc.start();
		  
		  // SlotPeriodic 
		  // 
		  sp.start();
		  
		  // Provera da li ima nekih koje ne rade kako treba 
		  // 
		  ck.start();
		  
		  // Garbage collector 
		  // 
		  System.gc();
		  
		  // Provera da li ima pristiglih uplata u poslednjih 15 minuta 
		  // 
		  sh.start();
		  
		  // Provera slot periodic 
		  // 
		  spec.start();
		  
		  // Izvestaj dnevni 
		  // 
		  pc.start();
		  
		  // Slot period brojac na kraju dana 
		  // 
		  spc.start();
		  
		  
		  // Proverava da li ima losih transakcija
		  //
		  bt.start();
		  
		  // Cekanje notify-a 
		  // 
		  Listener listener = new Listener(lConn);
		  listener.start(); 

		  // Slanje stanja na svaka dva sata
		  //
		  //tr.start();
		  
		  // Proveri da nije null 
		  // 
		  sendTransactionWithStatus0(lConn);
		  sendTransactionWithStatus0(lConn);
		  sendTransactionWithStatus0(lConn);
		  sendTransactionWithStatus0(lConn);
		  sendTransactionWithStatus0(lConn);
		  sendTransactionWithStatus0(lConn);
	}
}
