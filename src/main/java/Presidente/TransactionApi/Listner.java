package Presidente.TransactionApi;
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

class Listener implements Runnable {

   private final Connection conn;
   private final org.postgresql.PGConnection pgconn;
   public String transaction;
   private long startMilis = 0;
   
   Listener(Connection conn) throws SQLException {
      this.conn = conn;
      this.pgconn = (org.postgresql.PGConnection) conn;
      try (Statement stmt = conn.createStatement()) {
         stmt.execute("LISTEN row_updated");
      }
   }

   @Override
   public void run() {
      while (true) {
         notifyReturn();
         long millis=System.currentTimeMillis();  
         if (startMilis == 0) {
        	 startMilis = millis;
         }
         else {
        	 if (millis > startMilis + 120000) {
        		 /*new Thread(new Runnable() {
        			 public void Run() {
        				 proveri();
        			 }
        		 }).start();
        		 startMilis = millis;*/
        	 }
         }
      }
   }
   
   public void notifyReturn() 
   {   
       ExecutorService executor = Executors.newSingleThreadExecutor();
       Callable<String> callable = new Callable<String>() {
           @Override
           public String call() {
        	   try {
                 

                   org.postgresql.PGNotification[] notifications = pgconn.getNotifications();
                   if (Objects.nonNull(notifications)) {
                      for (org.postgresql.PGNotification notification : notifications) {
                         System.out.println("Got job: " + notification.getParameter());
                         transaction = notification.getParameter();
                         return transaction;
                         
                         // interesting async processing
                      }
                   }
                   // wait a while before checking again for new notifications
                   Thread.sleep(200);
                } catch (SQLException sqle) {
                   sqle.printStackTrace();
                } catch (InterruptedException ie) {
                   Thread.currentThread().interrupt();
                   ie.printStackTrace();
                }
			return "Proba";
           }
       };
       try {
	       Future<String> future = executor.submit(callable);
	       if(future.isDone()) {
	    	   future.get();
	       };
       }
       catch (Exception e) {
           e.printStackTrace();
       }
       finally {    	   
    	   executor.shutdown();
       }
   }
   
   
}

