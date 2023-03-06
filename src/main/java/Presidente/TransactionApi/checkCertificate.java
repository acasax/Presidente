package Presidente.TransactionApi;

import java.security.NoSuchAlgorithmException;
import javax.net.ssl.SSLContext;

public class checkCertificate extends Thread {
	public void run() {
		try {
            // Define the keystore type, path, and password
			
			String a = "============== Certificate ================\r\n";
			a += String.join(" ", SSLContext.getDefault().getSupportedSSLParameters().getProtocols());
			a += "\r\n=================================";
			System.out.print(a);

			try {
	            SSLContext.getInstance("TLSv1.3");
	            System.out.println("\r\nTLSv1.3 is supported.");
	            SSLContext sslContext = SSLContext.getInstance("TLSv1.3");
	            sslContext.init(null, null, null);
	        } catch (NoSuchAlgorithmException e) {
	            System.out.println("TLSv1.3 is not supported.");
	        }
			
			a = "============== Certificate ================\r\n";
			a += String.join(" ", SSLContext.getDefault().getSupportedSSLParameters().getProtocols());
			a += "\r\n=================================";
			System.out.print(a);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
