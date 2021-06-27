package Presidente.TransactionApi;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

class Listener implements Runnable {

   private final Connection conn;
   private final org.postgresql.PGConnection pgconn;

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
         try {
            // issue a dummy query to contact the backend
            // and receive any pending notifications.
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT 1");
            rs.close();
            stmt.close();

            org.postgresql.PGNotification[] notifications = pgconn.getNotifications();
            if (Objects.nonNull(notifications)) {
               for (org.postgresql.PGNotification notification : notifications) {
                  System.out.println("Got job: " + notification.getParameter());
                  // interesting async processing
               }
            }

            // wait a while before checking again for new notifications
            Thread.sleep(500);
         } catch (SQLException sqle) {
            sqle.printStackTrace();
         } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            ie.printStackTrace();
         }
      }
   }

}