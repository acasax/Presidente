package Presidente.TransactionApi;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

import org.json.JSONObject;

import com.impossibl.postgres.api.jdbc.PGConnection;
import com.impossibl.postgres.api.jdbc.PGNotificationListener;

import Presidente.TransactionApi.*;
import java.util.ArrayList;

public class App {
	static Listener listener = null;
	static ArrayList<Processing> lista = new ArrayList<>();
	static String notifyTransaction;
	static String urlL = "jdbc:postgresql://93.87.76.139:1521/accounting"; //sa lokaln emasine
	//static String url = "jdbc:postgresql://localhost:1521/accounting";
	static String user = "presidente";
	static String password = "Pr3z1d3nt3@Tr3ndPl@j!";
	
	static Object pgconn;
	static String transactionWithStatus0;
	static String transactionId;
	static String transactionPath;
	static String transactionWithtransactionId;
	static JSONObject transactionBody;
	static String transactionJSONError;

	static DbFunctions db = new DbFunctions();
	static Functions fun = new Functions();
	
	
	
	static Connection lConn;

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
	public static void sendTransaction(String transaction) throws SecurityException, IOException {
		try {
			if (transaction != null) {
				transactionId = fun.getTransansactionId(transaction, "s");
				transactionPath = fun.getTransansactionPath(transaction, "s");
				transactionBody = fun.checkJSONforSend(transaction, transactionPath);
				transactionJSONError = fun.getParamFromJson(transactionBody.toString(), "error");
				if (transactionJSONError != null) {
					fun.createLog(transactionJSONError); // kreira log fajl sa greskom o parametrima
				} else {
					// Procedura Set Status 10
					db.executeProcedure("CALL public.set_status_10_by_transaction_id('" + transactionId + "')", lConn);
					// Pokretanje procesa za odredjeni transaction id
					Processing newProcess = new Processing(transactionId, transactionPath, transactionBody, lConn);
					lista.add(newProcess);
					newProcess.start();
				}

			}
		} catch (SecurityException | IOException e) {
			fun.createLog(e.getMessage());
		}
	}

	// Funkcija koja kreira novi processing od transakcija koja je pronadjena u bazi
	// i koja ima status 0
	//
	public static void sendTransactionWithStatus0() throws SQLException, SecurityException, IOException {
		try {
			transactionWithStatus0 = db.executeFunction("SELECT public.get_json_by_status(0)", lConn,
					"get_json_by_status");
			while (transactionWithStatus0 != null) {
				transactionId = fun.getTransansactionId(transactionWithStatus0, "s");
				transactionPath = fun.getTransansactionPath(transactionWithStatus0, "s");
				transactionBody = fun.checkJSONforSend(transactionWithStatus0, transactionPath);
				transactionJSONError = fun.getParamFromJson(transactionBody.toString(), "error");
				if (transactionJSONError != null) {
					fun.createLog(transactionJSONError); // kreira log fajl sa greskom o parametrima
				} else {
					// Procedura Set Status 10
					db.executeProcedure("CALL public.set_status_10_by_transaction_id('" + transactionId + "')", lConn);
					// Pokretanje procesa za odredjeni transaction id
					Processing newProcess = new Processing(transactionId, transactionPath, transactionBody, lConn);
					lista.add(newProcess);
					newProcess.start();
					transactionWithStatus0 = db.executeFunction("SELECT public.get_json_by_status(0)", lConn,
							"get_json_by_status");
				}
			}
		} catch (SQLException | SecurityException | IOException e) {
			fun.createLog(e.getMessage());
		}
	}

	public static void main(String[] args)
			throws SQLException, InterruptedException, ExecutionException, SecurityException, IOException {
	
		db.asyconnect(urlL, user, password);
		lConn = DriverManager.getConnection(urlL, user, password);
		
		spStart sp      = new spStart(lConn);
		Check ck        = new Check(lConn);
		errorCheck ec   = new errorCheck(lConn);
		
		//Proverava da li ima log fajlova
		ec.start();
	
		//SlotPeriodic
		//
		sp.start();
			
		// Proveri da nije null
		sendTransactionWithStatus0();
		
		//Provera da li ima nekih koje ne rade kako treba
		//
		ck.start();
		
		// Cekanje notify-a
		Listener listener = new Listener(lConn);
		listener.start();

	}
}
