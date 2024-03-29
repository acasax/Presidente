package Presidente.TransactionApi;

import org.json.JSONObject;
import org.apache.http.client.ClientProtocolException;

import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

public class Processing extends Thread {

	private String TransactionId;
	private String TransactionPath;
	private JSONObject TransactionBody;
	private String URL = "https://api.uis.gov.rs/api/imports/v1/";
	private int Status;

	
	static String api_uid;
	static String response_text;
	static Long threadSleep;

	DbFunctions db = new DbFunctions();
	Functions fun = new Functions();
	private Connection conn;
	

	// Konsturktor osnovne klase
	public Processing(String TransactionId, String TransactionPath, JSONObject TransactionBody, Connection conn) {
		super();
		this.TransactionId   = TransactionId;
		this.TransactionPath = TransactionPath;
		this.TransactionBody = TransactionBody;
		this.conn = conn;
	}

	public String getTransactionId() {
		return TransactionId;
	}

	@Override
	public void run() {	

		try {
			// Kreira httpClient
			//
			CloseableHttpClient httpClient = HttpClients.createDefault();
			// Kreira httpPostRequest
			//
			HttpPost request = new HttpPost(URL + TransactionPath);

			System.out.println("Processing start");
			System.out.println("Processing TransactionId: " + TransactionId);
			//upisuje bodi koji je poslat u bazu
			//
			String apiJsonQuery = "UPDATE public.transactions SET api_json='" + TransactionBody.toString() + "' WHERE transaction_id = '"+ TransactionId +"';";
			try {
				db.executeQuery(apiJsonQuery, conn);
			} catch (SecurityException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			

			// Dodaje potrebne parametre u request
			//
			request.addHeader("x-api-key", "56443d42ce9c84e2dcc14f7bcc55bdbce21b0577458e18e82741979c9362c6e0"); // parametri
																												// za
																												// heder
			StringEntity se = new StringEntity(TransactionBody.toString(), "utf-8");
			se.setContentEncoding("UTF-8");
			se.setContentType("application/json");
			request.setEntity(se); // parametri
																				// body

			// Izvrsava request i ceka odgovor
			//
			CloseableHttpResponse response = httpClient.execute(request);

			try {

				Status = response.getStatusLine().getStatusCode(); // status odgovora
				// Body odgovora
				//
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					// return it as a String
					String result = EntityUtils.toString(entity);
					if (Status == 201) {
						api_uid = fun.getParamFromJson(result, "uuid");
					} else {
						response_text = result;
					}
				}

				// Ako je status uspesan menja status u bazi na 1 i gasi tred
				//
				if (Status == 201) {

					try {
						db.executeProcedure(
								"CALL public.set_status_1_by_transaction_id('" + TransactionId + "','" + api_uid + "')", conn);
					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					response.close();
					httpClient.close();
				} else {
					System.out.println("Processing else true");
					while (true) {
						// Proverava da li je radna za prekidanje
						//
						if (!Thread.interrupted()) {
							
							
							// Funkcija za api kaunter
							//
							threadSleep = fun.getApiCounter(TransactionId, db, conn);
							Thread.sleep(threadSleep);

							request = new HttpPost(URL + TransactionPath);

							// Dodaje potrebne parametre u request
							//
							request.addHeader("x-api-key",
									"56443d42ce9c84e2dcc14f7bcc55bdbce21b0577458e18e82741979c9362c6e0"); // parametri za
																											// heder
							se = new StringEntity(TransactionBody.toString(), "utf-8");
							se.setContentEncoding("UTF-8");
							se.setContentType("application/json");
							request.setEntity(se); // parametri
							
							response = httpClient.execute(request);

							Status = response.getStatusLine().getStatusCode();

							entity = response.getEntity();
							if (entity != null) {
								// return it as a String
								String result = EntityUtils.toString(entity);
								if (Status == 201) {
									api_uid = fun.getParamFromJson(result, "uuid");
								} else {
									response_text = result;
								}
							}

							if (Status == 201) {
								try {
									db.executeProcedure("CALL public.set_status_1_by_transaction_id('" + TransactionId + "','" + api_uid + "')", conn);
								} catch (SecurityException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (SQLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								response.close();
								httpClient.close();
								return;
							}else {
								try {
									db.executeProcedure("CALL public.set_status_11_by_transaction_id('" + TransactionId + "','"	+ response_text + "', '" + String.valueOf(Status) + "')", conn);
								} catch (SecurityException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (SQLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							request = null;
						}else {
							return;
						}

					}

				}
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				try {
					fun.createLog("Processing NumberFormatException e:" + e.getMessage() + "transactionId: " + TransactionId);
				} catch (SecurityException | IOException e2) {
					// TODO Auto-generated catch block
					fun.createLog("Processing SecurityException | IOException e:" + e2.getMessage() + "transactionId: " + TransactionId);
					e2.printStackTrace();
				} finally {
					response.close();
					httpClient.close();
				}
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				try {
					fun.createLog("Processing InterruptedException e:" + e.getMessage() + "transactionId: " + TransactionId);
				} catch (SecurityException | IOException e2) {
					// TODO Auto-generated catch block
					fun.createLog("Processing SecurityException | IOException e2:" + e2.getMessage() + "transactionId: " + TransactionId);
					e2.printStackTrace();
				} finally {
					response.close();
					httpClient.close();
				}
			} finally {
				response.close();
				httpClient.close();
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				fun.createLog("Processing ClientProtocolException e:" + e.getMessage()+ "transactionId: " + TransactionId);
				//httpClient.close();
			} catch (SecurityException | IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			try {
				fun.createLog("Processing IOException e:" + e.getMessage()+ "transactionId: " + TransactionId);
				//httpClient.close();
			} catch (SecurityException | IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
		}

	}

}
