package Presidente.TransactionApi;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

import com.impossibl.postgres.api.jdbc.PGConnection;
import com.impossibl.postgres.api.jdbc.PGNotificationListener;

import Presidente.TransactionApi.DbFunctions;

public class App 
{
	static Listener listener = null;
	
    public static void main( String[] args ) throws SQLException, InterruptedException, ExecutionException
    {
    	String url = "jdbc:postgresql://65.21.110.211:5432/accounting";  
		String user = "ensico";
		String password = "jflakj344*&^4J2fdHDSF&^FN";
		Object pgconn;
		String transactionWithStatus0;
		
		
        DbFunctions db = new DbFunctions(); 
        Functions fun  = new Functions();
        //db.connect();
        db.asyconnect();
        
        
        Connection lConn = DriverManager.getConnection(url, user, password);
       
        transactionWithStatus0 = db.executeFunction("SELECT public.get_json_by_status(0)", lConn, "get_json_by_status");
        
        while(transactionWithStatus0 != "0") {
        	db.executeFunction("SELECT public.get_json_by_status(0)", lConn, "get_json_by_status");
        	fun.getTransansactionId(transactionWithStatus0);
        }
        
        
        if (listener == null) {
        	listener = new Listener(lConn);
        	new Thread(listener).start();
        }
        
        db.executeProcedure("p_new_transaction", lConn);
        
    }
}
