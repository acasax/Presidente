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
    public static void main( String[] args ) throws SQLException, InterruptedException, ExecutionException
    {
    	String url = "jdbc:postgresql://65.21.110.211:5432/accounting";  
		String user = "presidente";
		String password = "test";
		
        DbFunctions db = new DbFunctions(); 
        //db.connect();
        db.asyconnect();
        
        
        Connection lConn = DriverManager.getConnection(url, user, password);
        Listener listener = new Listener(lConn);
        //listener.run();
        listener.run();
    }
}
