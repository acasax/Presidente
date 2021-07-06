package Presidente.TransactionApi;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

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
            Class.forName("org.postgresql.Driver"); 
 
        } catch (ClassNotFoundException e) {
            System.out.println("PostgreSQL JDBC driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Connection failure.");
            e.printStackTrace();
        }
	}
	
	public void asyconnect(String url, String user, String password) {
		
		try(Connection connection = DriverManager.getConnection(url, user, password);) {
			System.out.println("Connected asy to PostgreSQL database!");
		} catch (SQLException e) {
			  System.out.println("Connection failure.");
	          e.printStackTrace();
			
		}
	}
	
	
	public String executeProcedure(String procedureSQL, Connection connection) {
		 String result = "";
	        try {
	            CallableStatement properCase = connection.prepareCall(procedureSQL);
	            //properCase.setString(1, procedureName);
	            properCase.execute();
	            
	        } catch (SQLException e) {
	            System.out.println(e.getMessage());
	        }
	        return result;
	}
	
	
	
	public String executeFunction(String SQL, Connection connection, String functionName ) throws SQLException {
		try {
			String notSendtransaction;
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(SQL);
			if(resultSet != null) {
				while (resultSet.next()) {
					System.out.println(resultSet.getString(functionName));
					return notSendtransaction = resultSet.getString(functionName);
				}
			}else {
				return "0";
			}
		} catch (SQLException e) {
            System.out.println(e.getMessage());
            return e.getMessage();
        }
		return null; 	
	}
		
	
}
