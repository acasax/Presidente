package Presidente.TransactionApi;

public class spErrorCheck extends Thread {
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
