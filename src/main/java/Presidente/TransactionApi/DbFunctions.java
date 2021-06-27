package Presidente.TransactionApi;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.impossibl.postgres.jdbc.PGDriver;
import com.impossibl.postgres.jdbc.PGDataSource;
import com.impossibl.postgres.api.jdbc.PGConnection;
import com.impossibl.postgres.api.jdbc.PGNotificationListener;
import com.impossibl.postgres.jdbc.PGConnectionPoolDataSource;
import com.impossibl.postgres.jdbc.xa.PGXADataSource;
import java.sql.Connection;



public class DbFunctions {
	String url = "jdbc:postgresql://65.21.110.211:5432/accounting";  
	String user = "presidente";
	String password = "test";
	
	public void connect() {
		try (Connection connection = DriverManager.getConnection("jdbc:postgresql://65.21.110.211:5432/accounting", "presidente", "test")) {
			 
            System.out.println("Java JDBC PostgreSQL Example");
            // When this class first attempts to establish a connection, it automatically loads any JDBC 4.0 drivers found within 
            // the class path. Note that your application must manually load any JDBC drivers prior to version 4.0.
            Class.forName("org.postgresql.Driver"); 
 
            System.out.println("Connected to PostgreSQL database!");
            Statement statement = connection.createStatement();
            System.out.println("Reading car records...");
            System.out.printf("%-30.30s  %-30.30s%n", "Model", "Price");
            ResultSet resultSet = statement.executeQuery("SELECT * FROM public.cars");
            while (resultSet.next()) {
                System.out.printf("%-30.30s  %-30.30s%n", resultSet.getString("model"), resultSet.getString("price"));
            }
 
        } catch (ClassNotFoundException e) {
            System.out.println("PostgreSQL JDBC driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Connection failure.");
            e.printStackTrace();
        }
	}
	
	public void asyconnect() {
		String url = "jdbc:postgresql://65.21.110.211:5432/accounting";  
		String user = "presidente";
		String password = "test";
		try(Connection connection = DriverManager.getConnection(url, user, password);) {
			System.out.println("Connected asy to PostgreSQL database!");
		} catch (SQLException e) {
			  System.out.println("Connection failure.");
	          e.printStackTrace();
			
		}
	}
	
	
	
}
