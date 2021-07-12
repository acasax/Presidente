package Presidente.TransactionApi;
import java.io.IOException;
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
	/*static String url = "jdbc:postgresql://65.21.110.211:5432/accounting";
	static String user = "presidente";
	static String password = "test";*/
	static String url = "jdbc:postgresql://93.87.76.160:5432/accounting";
	static String user = "presidente";
	static String password = "testpass";
	Functions fun = new Functions();
	
	public void asyconnect(String url, String user, String password) throws SecurityException, IOException {
		
		try(Connection connection = DriverManager.getConnection(url, user, password);) {
			
		} catch (SQLException e) {
			 fun.createLog(e.getMessage());
		}
	}
	
	
	public String executeProcedure(String procedureSQL, Connection connection) throws SecurityException, IOException {
		 String result = "";
	        try {
	            CallableStatement properCase = connection.prepareCall(procedureSQL);
	            //properCase.setString(1, procedureName);
	            properCase.execute();
	            return result;
	        } catch (SQLException e) {
	            fun.createLog(e.getMessage());
	            return e.getMessage();
	        } 
	}
	
	
	
	public String executeFunction(String SQL, Connection connection, String functionName ) throws SQLException, SecurityException, IOException {
		try {
			String notSendtransaction;
			Statement statement = connection.createStatement();

			ResultSet resultSet = statement.executeQuery(SQL);
			if(resultSet != null /*resultSet != "0#]}"*/) {
				while (resultSet.next()) {
					return notSendtransaction = resultSet.getString(functionName);
				}
			}else {
				return "0";
			}
		} catch (SQLException e) {
			fun.createLog(e.getMessage());
            return e.getMessage();
        }
		return "";
		
	}
		
	
}
