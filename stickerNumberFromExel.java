package Presidente.TransactionApi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class stickerNumberFromExel extends Thread {
	DbFunctions db = new DbFunctions();
	Functions fun = new Functions();
	
	private static final long MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;
	private Connection conn;

	String producer_serial_number;
	String id_number;
	String getStickerNumber;
	
	String[] columns = { "sticker_number" };
	String[] columnsSum = { "suma" };
	String stricker_number;
	String[] parts;
	String sumDepositSql;
	String sumDeposit;
	String date = "";
	
    private static Date findNextDay(Date date)
    {
        return new Date(date.getTime() + MILLIS_IN_A_DAY);
    }

	public void run() {
		File file = new File("C:\\Users\\KORISNIK\\Desktop\\prezident\\stareuplate\\uplateStare.xlsx"); // creating a
																										// new file
		// instance
		try {
			FileInputStream fis = new FileInputStream(file);
			// obtaining bytes from the file
			// creating Workbook instance that refers to .xlsx file
			XSSFWorkbook wb = new XSSFWorkbook(fis);
			XSSFSheet sheet = wb.getSheetAt(0); // creating a Sheet object to retrieve object

			Iterator<Row> itr = sheet.iterator(); // iterating over excel file
			int rowNumber = 0;
			int j = 0;
			while (itr.hasNext()) {
				Row row = itr.next();
				if (rowNumber == 0) {
					rowNumber++;
					continue;
				}
				//Iterator<Cell> cellIterator = row.cellIterator(); // iterating over each column
				// while (cellIterator.hasNext()) {
				Cell cell = row.getCell(2);
				date = cell.getStringCellValue();
				date = date.replaceAll("\\s+","");
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
				Date convertedCurrentDate = sdf.parse(date);
				Date nextDay = findNextDay(convertedCurrentDate);
				String nextDate = sdf.format(nextDay);
				Cell cell1 = row.getCell(1);
				stricker_number = cell1.getStringCellValue();
				stricker_number = stricker_number.replaceAll("\\s+","");
				sumDepositSql = "SELECT  SUM(public.transactions.transaction_amount) as suma FROM public.transactions INNER JOIN public.machines ON public.transactions.machine_num_id = public.machines.id_number INNER JOIN public.transaction_types ON public.transactions.transaction_types = public.transaction_types.transaction_types WHERE public.transactions.transaction_time >= '"+ date +" 07:00:00' AND public.transactions.transaction_time <= '"+ nextDate +" 03:59:59' AND public.machines.sticker_number = '"+ stricker_number +"' AND public.transaction_types.path = 'slot/deposit'";
				sumDeposit = db.executeQuery1(sumDepositSql, "Gotovo je", columnsSum, conn);
				String[] parts = sumDeposit.split(":"); 
				Cell cell2 = row.getCell(4);
				String val = parts[1].replaceAll("\\s+","");
				cell2.setCellValue(val);
				System.out.print("Broj" + j + "vrednost" + val + "Datum" + date + "\r\n");
				j++;
				/*
				 * switch (cell.getCellType()) { case Cell.CELL_TYPE_STRING: // field that
				 * represents string cell type
				 * 
				 * //Dodavanje stricker number po macadresi //
				 * 
				 * producer_serial_number = cell.getStringCellValue(); getStickerNumber =
				 * "SELECT sticker_number, producer_serial_number FROM public.machines WHERE producer_serial_number='"
				 * + producer_serial_number + "'"; stricker_number =
				 * db.executeQuery1(getStickerNumber, "nema producer number id", columns );
				 * parts = stricker_number.split(":"); Cell cell1 = row.createCell(1);
				 * cell1.setCellValue(parts[1]);
				 * 
				 * break; case Cell.CELL_TYPE_NUMERIC: // field that represents number cell type
				 * Double producer_serial_numberd = cell.getNumericCellValue(); String
				 * doubleAsString = String.valueOf(producer_serial_numberd); int indexOfDecimal
				 * = doubleAsString.indexOf("."); String producesNumberDecimal =
				 * doubleAsString.substring(0, indexOfDecimal);
				 * System.out.print(producesNumberDecimal); getStickerNumber =
				 * "SELECT sticker_number, producer_serial_number FROM public.machines WHERE producer_serial_number='"
				 * + producesNumberDecimal + "'"; stricker_number =
				 * db.executeQuery1(getStickerNumber, "nema producer number id", columns );
				 * String[] parts = stricker_number.split(":"); Cell cell2 = row.createCell(1);
				 * cell2.setCellValue(parts[1]); break; default: }
				 */
				// }
			}
			fis.close();

			// Open an excel to write the data into workbook
			FileOutputStream fos = new FileOutputStream(file);

			// Write into workbook
			wb.write(fos);

			// close fileoutstream
			fos.close();
			
			System.out.print("Kraj");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}
}
