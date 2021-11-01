package Presidente.TransactionApi;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class Listener extends Thread {

   private final Connection conn;
   private final org.postgresql.PGConnection pgconn;
   public String transaction;
   private long startMilis = 0;
   
   Listener(Connection conn) throws SQLException {
		this.conn = conn;
		this.pgconn = (org.postgresql.PGConnection)conn;
		try {
			Statement stmt = conn.createStatement();
			stmt.execute("LISTEN row_updated");
			stmt.close();
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
	}

	public void run() {
		while (true) {
			try {
				notifyWaiter();
		         long millis=System.currentTimeMillis();  
		         if (startMilis == 0) {
		        	 startMilis = millis;
		         }
		         else {
		        	 if (millis > startMilis + 60000) { // vreme za proveru statusa 0
		        		 new Thread(new Runnable() {
		        			 public void run() {
		        				 try {
									App.sendTransactionWithStatus0();
									//U log fajlu da li se ovo desava
								} catch (SQLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (SecurityException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
		        			 }
		        		 }).start();
		        		 startMilis = millis;
		        	 }
		         }
				// wait a while before checking again for new
				// notifications
				Thread.sleep(500);
			} catch (SQLException sqle) {
				sqle.printStackTrace();
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			} catch (SecurityException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

   
   public void notifyWaiter() throws SQLException, SecurityException, IOException {
	   org.postgresql.PGNotification[] notifications = pgconn.getNotifications();
       if (Objects.nonNull(notifications)) {
          for (org.postgresql.PGNotification notification : notifications) {
             transaction = notification.getParameter();
             App.sendTransaction(transaction);
          }
       }
   }
   
   
}
