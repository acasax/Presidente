package Presidente.TransactionApi;

import org.json.JSONObject;
import org.apache.http.HttpHeaders;
import org.apache.http.client.ClientProtocolException;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

public class Processing extends Thread {

	private String TransactionId;
	private String TransactionPath;
	private JSONObject TransactionBody;
	private String URL = "https://api.uis.gov.rs/api/imports/v1/";
	private int Status;
	static String url = "jdbc:postgresql://65.21.110.211:5432/accounting";
	static String user = "presidente";
	static String password = "test";
	static String api_uid;
	
	DbFunctions db = new DbFunctions();
	Functions fun = new Functions();
	static Connection lConn;

	// Konsturktor osnovne klase
	public Processing(String TransactionId, String TransactionPath, JSONObject TransactionBody) {
		super();
		this.TransactionId = TransactionId;
		this.TransactionPath = TransactionPath;
		this.TransactionBody = TransactionBody;
	}

	// dodaj getere i setere

	@Override
	public void run() {
		// TODO
		
		try {
			lConn = DriverManager.getConnection(url, user, password);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		CloseableHttpClient httpClient = HttpClients.createDefault();

		try {

			HttpPost request = new HttpPost(URL + TransactionPath);

			// add request headers
			request.addHeader("x-api-key", "56443d42ce9c84e2dcc14f7bcc55bdbce21b0577458e18e82741979c9362c6e0");
			request.setEntity(new StringEntity(TransactionBody.toString(), ContentType.APPLICATION_JSON));

			CloseableHttpResponse response = httpClient.execute(request);

			try {

				// Get HttpResponse Status
				System.out.println(response.getStatusLine().getStatusCode()); // 200
				System.out.println(response.getStatusLine().getReasonPhrase()); // OK

				Status = response.getStatusLine().getStatusCode();

				HttpEntity entity = response.getEntity();
				if (entity != null) {
					// return it as a String
					String result = EntityUtils.toString(entity);
					api_uid = fun.getParamFromJson(result, "uuid");
					System.out.println(result);
				}

				if (Status == 201) {
					db.executeProcedure("CALL public.set_status_1_by_transaction_id('" + TransactionId + "','"+ api_uid +"')", lConn);
				}

			} finally {
				response.close();
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
