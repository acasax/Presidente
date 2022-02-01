package Presidente.TransactionApi;

import java.io.IOException;
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
	static constError ce = new constError();

	// Da li postoji proces sa zadatim transaction_id koji radi
	public static Processing nadjiProcessing(String transactionId) {
		for (int i = 0; i < lista.size(); i++) {
			if (lista.get(i).getTransactionId().equals(transactionId)) {
				Processing badProcess = lista.get(i);
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
	public static void sendTransaction(String transaction)
			throws SecurityException, IOException, SQLException, ParseException {
		if (fun.workTime()) {
			try {
				if (transaction != null) {
					transactionId = fun.getTransansactionId(transaction, "s");
					transactionPath = fun.getTransansactionPath(transaction, "s");
					transactionBody = fun.checkJSONforSend(transaction, transactionPath, db);
					transactionJSONError = fun.getParamFromJson(transactionBody.toString(), "error");
					transactionSendingStatus = fun.getParamFromJson(transactionBody.toString(), "send_status");
					if (transactionJSONError != null || transactionSendingStatus != null) {
						if (transactionJSONError != null) {
							fun.createLog(transactionJSONError);
						} else {
							fun.createLog(transactionSendingStatus);
						}
					} else {
						// Procedura Set Status 10
						db.executeProcedure("CALL public.set_status_10_by_transaction_id('" + transactionId + "')");
						// Pokretanje procesa za odredjeni transaction id
						Processing newProcess = new Processing(transactionId, transactionPath, transactionBody);
						lista.add(newProcess);
						newProcess.start();

					}

				}
			} catch (SecurityException | IOException e) {
				fun.createLog(ce.sendTransaction);
			}
		}
	}

	// Funkcija koja kreira novi processing od transakcija koja je pronadjena u bazi
	// i koja ima status 0
	//
	public static void sendTransactionWithStatus0()
			throws SQLException, SecurityException, IOException, ParseException {
		if (fun.workTime()) {
			try {
				transactionWithStatus0 = db.executeFunction("SELECT public.get_json_by_status(0)",
						"get_json_by_status");
				while (transactionWithStatus0 != null) {
					transactionId = fun.getTransansactionId(transactionWithStatus0, "s");
					transactionPath = fun.getTransansactionPath(transactionWithStatus0, "s");
					transactionBody = fun.checkJSONforSend(transactionWithStatus0, transactionPath, db);
					transactionSendingStatus = fun.getParamFromJson(transactionBody.toString(), "send_status");
					if (transactionJSONError != null || transactionSendingStatus != null) {
						if (transactionJSONError != null) {
							fun.createLog(transactionJSONError);
						} else {
							fun.createLog(transactionSendingStatus);
						}
					} else {
						// Procedura Set Status 10
						db.executeProcedure("CALL public.set_status_10_by_transaction_id('" + transactionId + "')");
						// Pokretanje procesa za odredjeni transaction id
						Processing newProcess = new Processing(transactionId, transactionPath, transactionBody);
						lista.add(newProcess);
						newProcess.start();
						transactionWithStatus0 = db.executeFunction("SELECT public.get_json_by_status(0)",
								"get_json_by_status");
					}
				}
			} catch (SQLException | SecurityException | IOException e) {
				fun.createLog(
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
		
		stickerNumberFromExel snfx = new stickerNumberFromExel();
		snfx.start();
		/*
		 * Connection lConn = db.asyconnect();
		 * 
		 * slotPeriodicCheck spc = new slotPeriodicCheck(); spStart sp = new spStart();
		 * Check ck = new Check(); ErrorCheck ec = new ErrorCheck(); locationCheck lc =
		 * new locationCheck(); shitsHapend sh = new shitsHapend(); spErrorCheck spec =
		 * new spErrorCheck(); paymentCheck pc = new paymentCheck(); apiUuidStatus aus =
		 * new apiUuidStatus();
		 * 
		 * System.out.print("Pokrenuto"); // Email za proveru aplikacije //
		 * fun.sendEmail("Aplikacija se startovala u: " + LocalDateTime.now(),
		 * "resivojee@gmail.com", "Pokretanje aplikacije");
		 * 
		 * // Proverava da li ima log fajlova // ec.start();
		 * 
		 * // Provera lokacija u poslednja dva sata // lc.start();
		 * 
		 * // SlotPeriodic // sp.start();
		 * 
		 * // Proveri da nije null // sendTransactionWithStatus0();
		 * 
		 * // Provera da li ima nekih koje ne rade kako treba // ck.start();
		 * 
		 * // Garbage collector // System.gc();
		 * 
		 * // Provera da li ima pristiglih uplata u poslednjih 15 minuta // sh.start();
		 * 
		 * // Provera slot periodic // spec.start();
		 * 
		 * // Izvestaj dnevni // pc.start();
		 * 
		 * // Slot period brojac na kraju dana // spc.start();
		 * 
		 * // Proverava da li slanje ka upravi radi // aus.start();
		 * 
		 * // Cekanje notify-a // Listener listener = new Listener(lConn);
		 * listener.start();
		 */

	}
}
