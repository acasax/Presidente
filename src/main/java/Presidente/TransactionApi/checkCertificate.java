package Presidente.TransactionApi;

import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

public class checkCertificate extends Thread {
	public void run() {
		try {
            // Define the keystore type, path, and password
			try {
	            SSLContext.getInstance("TLSv1.3");
	            System.out.println("TLSv1.3 is supported.");
	            SSLContext sslContext = SSLContext.getInstance("TLSv1.3");
	            sslContext.init(null, null, null);
	        } catch (NoSuchAlgorithmException e) {
	            System.out.println("TLSv1.3 is not supported.");
	        }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
