package Presidente.TransactionApi;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.postgresql.ds.PGPoolingDataSource;



public class DbFunctions {
	
	Functions fun = new Functions();
	ConstError ce = new ConstError();
	
	@SuppressWarnings("deprecation")
	public Connection asyconnect() throws SecurityException, IOException, ParseException {
	    int retries = 0;
	    int maxRetries = 5;
	    int delaySeconds = 10;

	    while (retries < maxRetries) {
	        if (fun.workTime()) {
	            try{
	                PGPoolingDataSource source = new PGPoolingDataSource();
	                source.setServerNames(new String[] {"93.87.76.139:1521"});
	                source.setDatabaseName("accounting");
	                source.setUser("presidente");
	                source.setPassword("Pr3z1d3nt3@Tr3ndPl@j!");
	                source.setMaxConnections(350);
	                Connection connection = source.getConnection();
	                return connection;
	            } catch (SQLException e) {
	                fun.createLogDb("DbFunctions asyconnect: " + ce.asyconnect);
	                fun.sendEmail("konekcija na bazu je prekinuta", "presidenteapp@yahoo.com", "Nema konekcije na bazu");

	                // Wait for a specified amount of time before retrying
	                try {
	                    Thread.sleep(delaySeconds * 1000);
	                } catch (InterruptedException ex) {
	                    ex.printStackTrace();
	                }

	                // Increment the retry counter
	                retries++;
	            }
	        } else {
	            // Wait for a specified amount of time before retrying
	            try {
	                Thread.sleep(delaySeconds * 1000);
	            } catch (InterruptedException ex) {
	                ex.printStackTrace();
	            }
	        }
	    }

	    // If the connection was not established after the maximum number of retries, throw an exception
	    throw new RuntimeException("Failed to connect to database after " + retries + " attempts.");
	}
	
	

	public Connection newAsyconnect() throws SQLException, SecurityException, IOException, ParseException {
	    int retries = 0;
	    int maxRetries = 5;
	    int delaySeconds = 10;

	    HikariConfig config = new HikariConfig();
	    config.setJdbcUrl("jdbc:postgresql://93.87.76.139:1521/accounting");
	    config.setUsername("presidente");
	    config.setPassword("Pr3z1d3nt3@Tr3ndPl@j!");
	    config.setMaximumPoolSize(450);
	    HikariDataSource dataSource = new HikariDataSource(config);
	    try {
	    	while (retries < maxRetries) {
		        if (fun.workTime()) {
		            try (Connection connection = dataSource.getConnection()) {
		                return connection;
		            } catch (SQLException e) {
		                fun.createLogDb("DbFunctions newAsyconnect: " + ce.asyconnect);
		                fun.sendEmail("konekcija na bazu je prekinuta", "presidenteapp@yahoo.com", "Nema konekcije na bazu");

		                // Wait for a specified amount of time before retrying
		                try {
		                    Thread.sleep(delaySeconds * 1000);
		                } catch (InterruptedException ex) {
		                    ex.printStackTrace();
		                }

		                // Increment the retry counter
		                retries++;
		            }
		        } else {
		            // Wait for a specified amount of time before retrying
		            try {
		                Thread.sleep(delaySeconds * 1000);
		            } catch (InterruptedException ex) {
		                ex.printStackTrace();
		            }
		        }
		    }
	    } catch(Exception e) {
	    	 // If the connection was not established after the maximum number of retries, throw an exception
		    throw new RuntimeException("Failed to connect to database after " + retries + " attempts.");
	    }
	    finally {
	        //dataSource.close();
	    }
		return null;
	}


	
	public String executeQuery(String procedureSQL, Connection conn) throws SecurityException, IOException, SQLException {
	    Statement stmnt = null;
	    try {
	        stmnt = conn.createStatement();
	        stmnt.executeUpdate(procedureSQL);
	        return "";
	    } catch (SQLException e) {
	        fun.createLogDb("DbFunctions executeQuery: " + ce.executeQuery + procedureSQL);
	        return e.getMessage();
	    } finally {
	        // close the statement and connection
	        if (stmnt != null) {
	            stmnt.close();
	        }
	    }
	}

	
	
	public String executeProcedure(String procedureSQL, Connection conn) throws SecurityException, IOException, SQLException {
	    String result = "";
	    System.out.println("Processing executeProcedure procedureSQL: " + procedureSQL);
	    try {
	        CallableStatement properCase = conn.prepareCall(procedureSQL);
	        //properCase.setString(1, procedureName);
	        properCase.execute();
	        return result;
	    } catch (SQLException e) {
	        fun.createLogDb("DbFunctions executeProcedure: " + ce.executeProcedure + procedureSQL);
	        return e.getMessage();
	    } finally {
	    	
	    }
	}

	
	
	
	public String executeFunction(String SQL, String functionName, Connection conn) throws SQLException, SecurityException, IOException {
	    Statement statement = null;
	    ResultSet resultSet = null;
	    System.out.println("Processing executeFunction SQL: " + SQL);
	    try {
	        statement = conn.createStatement();
	        resultSet = statement.executeQuery(SQL);
	        if(resultSet != null) {
	            while (resultSet.next()) {
	                return resultSet.getString(functionName);
	            }
	        } else {
	            return null;
	        }
	    } catch (SQLException e) {
	        fun.createLogDb("DbFunctions executeFunction: " + ce.executeFunction + SQL);
	        return e.getMessage();
	    } finally {
	        if (resultSet != null) {
	            try {
	                resultSet.close();
	            } catch (SQLException e) {
	                // log or handle exception, if desired
	            	fun.createLogDb("DbFunctions executeFunction resultSet closing: " + ce.executeFunction + SQL);
	            }
	        }
	        if (statement != null) {
	            try {
	                statement.close();
	            } catch (SQLException e) {
	                // log or handle exception, if desired
	            	fun.createLogDb("DbFunctions executeFunction statement closing: " + ce.executeFunction + SQL);
	            }
	        }
	    }
	    return null;
	}

	
	public String executeQuery1(String SQL, String message, String[] params, Connection conn) throws SecurityException, IOException, SQLException {
	    String returnMsg = "";
	    Statement stmnt = null;
	    ResultSet resultSet = null;
	    try {
	        stmnt = conn.createStatement();
	        resultSet = stmnt.executeQuery(SQL);
	        if(resultSet != null) {
	            while (resultSet.next()) {
	                for(int i = 0; i < params.length; i++) {
	                    returnMsg = returnMsg + params[i] + ": " + resultSet.getString(params[i]) + " " + "\r\n";
	                }
	                returnMsg = returnMsg + "\r\n";
	            }
	            return returnMsg;
	        } else {
	            return message;
	        }
	    } catch (SQLException e) {
	        fun.createLogDb("DbFunctions executeQuery1: " + ce.executeQuery1 + SQL);
	        return e.getMessage();
	    } finally {
	        // close the result set, statement, and connection in the finally block
	    	if (resultSet != null) {
	            try {
	                resultSet.close();
	            } catch (SQLException e) {
	                // log or handle exception, if desired
	            	fun.createLogDb("DbFunctions executeQuery1 resultSet closing: " + ce.executeFunction + SQL);
	            }
	        }
	        if (stmnt != null) {
	            try {
	            	stmnt.close();
	            } catch (SQLException e) {
	                // log or handle exception, if desired
	            	fun.createLogDb("DbFunctions executeQuery1 statement closing: " + ce.executeFunction + SQL);
	            }
	        }
	    }
	}

	
	public String executeQuery2(String SQL, String message, String[] params, Connection conn) throws SecurityException, IOException, SQLException {
	    String returnMsg = "";
	    try {
	        Statement stmnt = null;
	        stmnt = conn.createStatement();
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
	            return returnMsg;
	        } else {
	            return message;
	        }
	    } catch (SQLException e) {
	        fun.createLogDb("DbFunctions executeQuery2: " + ce.executeQuery2 + SQL);
	        return e.getMessage();
	    } finally {
	    	
	    }
	}

	
	public String executeQuery3(String SQL, String[] params, Connection conn) throws SecurityException, IOException, SQLException {
		String returnMsg = "";
		 try {
		        Statement stmnt = null;
		        stmnt = conn.createStatement();
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
					return returnMsg;
				}else {
					return "";
				}
		    } catch (SQLException e) {
		    	fun.createLogDb("DbFunctions executeQuery3: " + ce.executeQuery3 + SQL);
	            return e.getMessage();
		    } finally {
		    	
		    }
	}

	
	public ResultSet executeQuery4(Connection conn, String SQL) throws SQLException, SecurityException, IOException {
	    Statement stmnt = null;
	    ResultSet resultSet = null;
	    try {
	        stmnt = conn.createStatement();
	        resultSet = stmnt.executeQuery(SQL);
	        return resultSet;
	    } catch (SQLException e) {
	        throw e;
	    } finally {
	    	if (resultSet != null) {
	            try {
	                resultSet.close();
	            } catch (SQLException e) {
	                // log or handle exception, if desired
	            	fun.createLogDb("DbFunctions executeQuery4 resultSet closing: " + ce.executeFunction + SQL);
	            }
	        }
	        if (stmnt != null) {
	            try {
	            	stmnt.close();
	            } catch (SQLException e) {
	                // log or handle exception, if desired
	            	fun.createLogDb("DbFunctions executeQuery4 statement closing: " + ce.executeFunction + SQL);
	            }
	        }
	       
	    }
	}

		
	
}
