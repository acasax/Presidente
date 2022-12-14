package Presidente.TransactionApi;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;

import org.postgresql.ds.PGPoolingDataSource;



public class DbFunctions {
	
	Functions fun = new Functions();
	ConstError ce = new ConstError();
	
	private static String urlL = "jdbc:postgresql://93.87.76.139:1521/accounting"; //sa lokalne masine
	//static String url = "jdbc:postgresql://localhost:1521/accounting";
	private static String user = "presidente";
	private static String password = "Pr3z1d3nt3@Tr3ndPl@j!";
	
	/*static Connection lConn;
	
	lConn = DriverManager.getConnection(urlL, user, password);*/
	
	@SuppressWarnings("deprecation")
	public Connection asyconnect() throws SecurityException, IOException, ParseException {
		if(fun.workTime()) {
			try{
				PGPoolingDataSource source = new PGPoolingDataSource();
				source.setServerNames(new String[] {"93.87.76.139:1521"});
				source.setDatabaseName("accounting");
				source.setUser("presidente");
				source.setPassword("Pr3z1d3nt3@Tr3ndPl@j!");
				source.setMaxConnections(100);
				Connection connection = source.getConnection();
				return connection;
			} catch (SQLException e) {
				 fun.createLogDb(ce.asyconnect);
				 fun.sendEmailYahho("konekcija na bazu je prekinuta", "presidenteapp@yahoo.com", "Nema konekcije na bazu");
			}
		}
		return null;
	}
	
	public String executeQuery(String procedureSQL) throws SecurityException, IOException, SQLException {
		Connection lConn = DriverManager.getConnection(urlL, user, password);
		 try {
		        Statement stmnt = null;
		        stmnt = lConn.createStatement();
		        stmnt.executeUpdate(procedureSQL);
		        lConn.close();
		        return "";
		    } catch (SQLException e) {
		    	lConn.close();
		    	fun.createLogDb(ce.executeQuery + procedureSQL);
	            return e.getMessage();
		    }

	}
	
	
	public String executeProcedure(String procedureSQL) throws SecurityException, IOException, SQLException {
		 Connection lConn = DriverManager.getConnection(urlL, user, password);
		 String result = "";
	        try {
	            CallableStatement properCase = lConn.prepareCall(procedureSQL);
	            //properCase.setString(1, procedureName);
	            properCase.execute();
	            lConn.close();
	            return result;
	        } catch (SQLException e) {
	        	lConn.close();
	        	fun.createLogDb(ce.executeProcedure + procedureSQL);
	            return e.getMessage();
	        } 
	}
	
	
	
	public String executeFunction(String SQL, String functionName ) throws SQLException, SecurityException, IOException {
		Connection lConn = DriverManager.getConnection(urlL, user, password);
		try {
			Statement statement = lConn.createStatement();
			ResultSet resultSet = statement.executeQuery(SQL);
			if(resultSet != null) {
				while (resultSet.next()) {
					lConn.close();
					return resultSet.getString(functionName);
				}
			}else {
				lConn.close();
				return null;
			}
		} catch (SQLException e) {
			lConn.close();
			fun.createLogDb(ce.executeFunction + SQL);
            return e.getMessage();
        }
		return null;
		
	}
	
	public String executeQuery1(String SQL, String message, String[] params) throws SecurityException, IOException, SQLException {
		Connection lConn = DriverManager.getConnection(urlL, user, password);
		String returnMsg = "";
		 try {
		        Statement stmnt = null;
		        stmnt = lConn.createStatement();
		    	ResultSet resultSet = stmnt.executeQuery(SQL);
				if(resultSet != null) {
					while (resultSet.next()) {
						for(int i = 0; i < params.length; i++) {
							returnMsg = returnMsg + params[i] + ": " + resultSet.getString(params[i]) + " " + "\r\n";
						}
						returnMsg = returnMsg + "\r\n";
					}
					resultSet.close();
					stmnt.cancel();
					lConn.close();
					return returnMsg;
				}else {
					lConn.close();
					return message;
				}
		    } catch (SQLException e) {
		    	lConn.close();
		    	fun.createLogDb(ce.executeQuery1 + SQL);
	            return e.getMessage();
		    }

	}
	
	public String executeQuery2(String SQL, String message, String[] params) throws SecurityException, IOException, SQLException {
		Connection lConn = DriverManager.getConnection(urlL, user, password);
		String returnMsg = "";
		 try {
		        Statement stmnt = null;
		        stmnt = lConn.createStatement();
		    	ResultSet resultSet = stmnt.executeQuery(SQL);
				if(resultSet != null) {
					while (resultSet.next()) {
						for(int i = 0; i < params.length; i++) {
							returnMsg = returnMsg + resultSet.getString(params[i]);
						}
						returnMsg = returnMsg + "\r\n";
					}
					resultSet.close();
					stmnt.cancel();
					lConn.close();
					return returnMsg;
				}else {
					lConn.close();
					return message;
				}
		    } catch (SQLException e) {
		    	lConn.close();
		    	fun.createLogDb(ce.executeQuery1 + SQL);
	            return e.getMessage();
		    }

	}
	
	public String executeQuery3(String SQL, String[] params) throws SecurityException, IOException, SQLException {
		Connection lConn = DriverManager.getConnection(urlL, user, password);
		String returnMsg = "";
		 try {
		        Statement stmnt = null;
		        stmnt = lConn.createStatement();
		    	ResultSet resultSet = stmnt.executeQuery(SQL);
				if(resultSet != null) {
					while (resultSet.next()) {
						for(int i = 0; i < params.length; i++) {
							returnMsg =  resultSet.getString(params[i]);
						}
						//returnMsg = returnMsg + "\r\n";
					}
					resultSet.close();
					stmnt.cancel();
					lConn.close();
					return returnMsg;
				}else {
					lConn.close();
					return "";
				}
		    } catch (SQLException e) {
		    	lConn.close();
		    	fun.createLogDb(ce.executeQuery1 + SQL);
	            return e.getMessage();
		    }

	}
	
	public ResultSet executeQuery4(String SQL) throws SecurityException, IOException, SQLException {
		Connection lConn = DriverManager.getConnection(urlL, user, password);

		 try {
		        Statement stmnt = null;
		        stmnt = lConn.createStatement();
		    	ResultSet resultSet = stmnt.executeQuery(SQL);
				//resultSet.close();
				stmnt.cancel();
				lConn.close();
				return resultSet;
		    } catch (SQLException e) {
		    	lConn.close();
		    	fun.createLogDb(ce.executeQuery4 + SQL);
		    }
		return null;

	}
		
	
}
