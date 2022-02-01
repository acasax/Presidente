package Presidente.TransactionApi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class stickerNumberFromExel extends Thread {
	DbFunctions db = new DbFunctions();
	Functions fun = new Functions();

	private static String urlL = "jdbc:postgresql://93.87.76.139:1521/accounting"; // sa lokalne masine
	private static String user = "presidente";
	private static String password = "Pr3z1d3nt3@Tr3ndPl@j!";

	String producer_serial_number;
	String id_number;
	String getStickerNumber;
	String[] columns = {"sticker_number"};
	String stricker_number;
	String[] parts;
	public void run() {
		File file = new File("C:\\Users\\KORISNIK\\Desktop\\prezident\\stareuplate\\uplateStare.xlsx"); // creating a new file
		// instance
		try {
			FileInputStream fis = new FileInputStream(file);
			 // obtaining bytes from the file
			// creating Workbook instance that refers to .xlsx file
			XSSFWorkbook wb = new XSSFWorkbook(fis);
			XSSFSheet sheet = wb.getSheetAt(0); // creating a Sheet object to retrieve object
			

			
			Iterator<Row> itr = sheet.iterator(); // iterating over excel file
			int rowNumber = 0;
			while (itr.hasNext()) {
				Row row = itr.next();
				if (rowNumber == 0) {
				    rowNumber++;
				    continue;
				}
				Iterator<Cell> cellIterator = row.cellIterator(); // iterating over each column
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					switch (cell.getCellType()) {
					case Cell.CELL_TYPE_STRING: // field that represents string cell type
						System.out.print(cell.getStringCellValue());
						producer_serial_number = cell.getStringCellValue();
						getStickerNumber = "SELECT sticker_number, producer_serial_number FROM public.machines WHERE producer_serial_number='" + producer_serial_number + "'";
						stricker_number = db.executeQuery1(getStickerNumber, "nema producer number id", columns );
						parts = stricker_number.split(":");
						Cell cell1 = row.createCell(1);
			            cell1.setCellValue(parts[1]);
						break;
					case Cell.CELL_TYPE_NUMERIC: // field that represents number cell type
						Double producer_serial_numberd = cell.getNumericCellValue();
						String doubleAsString = String.valueOf(producer_serial_numberd);
						int indexOfDecimal = doubleAsString.indexOf(".");
						String producesNumberDecimal = doubleAsString.substring(0, indexOfDecimal);
						System.out.print(producesNumberDecimal);
						getStickerNumber = "SELECT sticker_number, producer_serial_number FROM public.machines WHERE producer_serial_number='" + producesNumberDecimal + "'";
						stricker_number = db.executeQuery1(getStickerNumber, "nema producer number id", columns );
						String[] parts = stricker_number.split(":");
						Cell cell2 = row.createCell(1);
			            cell2.setCellValue(parts[1]);
						break;
					default:
					}
				}
			}
			fis.close();
			
		    //Open an excel to write the data into workbook
			FileOutputStream fos = new FileOutputStream(file);

		    //Write into workbook
		    wb.write(fos);

		    //close fileoutstream
		    fos.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
