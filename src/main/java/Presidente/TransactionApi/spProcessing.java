package Presidente.TransactionApi;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class spProcessing extends Thread {
	int reportIndex;
	JSONObject slotPeriodicBody;
	
	private String URL = "https://api.uis.gov.rs/api/imports/v1/";
	private String TransactionPath = "slot-periodic";
	DbFunctions db = new DbFunctions();
	Functions fun = new Functions();
	private int Status;
    private Connection conn;
	static String api_uid;
	static String response_text;
	static String threadSleep;

	// Konsturktor osnovne klase
	//
	public spProcessing(int reportIndex, JSONObject slotPeriodicBody, Connection conn) {
		super();
		this.reportIndex      = reportIndex;
		this.slotPeriodicBody = slotPeriodicBody;
		this.conn = conn;
	}

	// geterr za report index
	//
	public int getReportIndex() {
		return reportIndex;
	}

	@Override
	public void run() {
		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {

			//upisuje bodi koji je poslat u bazu
			//
			String apiJsonQuery = "UPDATE public.slot_periodic_h SET api_json='" + slotPeriodicBody.toString() + "' WHERE report_index = '"+ reportIndex +"';";
			try {
				db.executeQuery(apiJsonQuery, conn);
			} catch (SecurityException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			fun.checkSpJSONforSend(slotPeriodicBody.toString());
			// Kreira httpClient
			//
			CloseableHttpClient httpClient = HttpClients.createDefault();
			// Kreira httpPostRequest
			//
			HttpPost request = new HttpPost(URL + TransactionPath);

			// Dodaje potrebne parametre u request
			//
			request.addHeader("x-api-key", "56443d42ce9c84e2dcc14f7bcc55bdbce21b0577458e18e82741979c9362c6e0"); // parametri
																												// za
																												// heder
			request.setEntity(new StringEntity(slotPeriodicBody.toString(), ContentType.APPLICATION_JSON)); // parametri
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
						db.executeProcedure("CALL public.set_sp_status_1_by_report_index('" + reportIndex + "','" + api_uid + "')", conn);
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

					while (true) {
						// Proverava da li je radna za prekidanje
						//
						if (!Thread.interrupted()) {

							// Funkcija za api kaunter
							//
							threadSleep = fun.getSpApiCounter(reportIndex, db, conn);
							Thread.sleep(Long.parseLong(threadSleep));

							request = new HttpPost(URL + TransactionPath);

							// Dodaje potrebne parametre u request
							//
							request.addHeader("x-api-key",
									"56443d42ce9c84e2dcc14f7bcc55bdbce21b0577458e18e82741979c9362c6e0"); // parametri za
																											// heder
							request.setEntity(
									new StringEntity(slotPeriodicBody.toString(), ContentType.APPLICATION_JSON)); // parametri
																													// body
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
									db.executeProcedure("CALL public.set_sp_status_1_by_report_index('" + reportIndex + "','" + api_uid + "')", conn);
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
							} else {
								try {
									db.executeProcedure("CALL public.set_sp_status_11_by_report_index('" + reportIndex + "','" + response_text + "'," + String.valueOf(Status) + "')", conn);
								} catch (SecurityException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (SQLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							request = null;
						} else {
							return;
						}

					}

				}
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				try {
					fun.createLog("spProcessing NumberFormatException e:" + e.getMessage());
				} catch (SecurityException | IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				try {
					fun.createLog("spProcessing InterruptedException e:" + e.getMessage());
				} catch (SecurityException | IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
			} finally {
				response.close();
			}
		} catch (ClientProtocolException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
			try {
				fun.createLog("spProcessing ClientProtocolException e:" + e2.getMessage());
			} catch (SecurityException | IOException e4) {
				// TODO Auto-generated catch block
				e4.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
			try {
				fun.createLog("spProcessing IOException e:" + e.getMessage());
			} catch (SecurityException | IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
		}

	}

}
