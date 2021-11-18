package Presidente.TransactionApi;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class ErrorCheck extends Thread {
	
	Functions fun = new Functions();
	@Override
	public void run() {
		while(true) {
			fun.checkIsLogExist("logs");
			try {
				Thread.sleep(7200000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
