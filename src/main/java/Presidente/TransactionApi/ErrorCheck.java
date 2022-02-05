package Presidente.TransactionApi;

import java.text.ParseException;

public class ErrorCheck extends Thread {
	
	Functions fun = new Functions();
	@Override
	public void run() {
		while(true) {
			try {
				if(fun.workTime()) {
					fun.checkIsLogExist("logs");
					try {
						Thread.sleep(7200000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
