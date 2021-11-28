package Presidente.TransactionApi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class updateMachines extends Thread {

	DbFunctions db = new DbFunctions();
	Functions fun = new Functions();

	private static String urlL = "jdbc:postgresql://93.87.76.139:1521/accounting"; // sa lokalne masine
	private static String user = "presidente";
	private static String password = "Pr3z1d3nt3@Tr3ndPl@j!";

	String sticker_number;
	String id_number;

	String selectMachineQuery = "SELECT sticker_number FROM public.machines where machine_id_number is null limit 1";

	String updateMachineQuery = "UPDATE public.machines SET  machine_id_number= ''	WHERE sticker_number = '';";
	String[] columns = { "sticker_number" };

	public void run() {
		while (true) {
			try {
				File file = new File("C:\\Users\\KORISNIK\\Desktop\\prezident\\automati.xlsx"); // creating a new file
																								// instance
				FileInputStream fis = new FileInputStream(file); // obtaining bytes from the file
				// creating Workbook instance that refers to .xlsx file
				XSSFWorkbook wb = new XSSFWorkbook(fis);
				XSSFSheet sheet = wb.getSheetAt(0); // creating a Sheet object to retrieve object
				Iterator<Row> itr = sheet.iterator(); // iterating over excel file
				int j = 0;
				while (itr.hasNext()) {
					Row row = itr.next();
					Iterator<Cell> cellIterator = row.cellIterator(); // iterating over each column
					int i = 0;
					while (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();
						switch (cell.getCellType()) {
						case Cell.CELL_TYPE_STRING: // field that represents string cell type
							System.out.print(cell.getStringCellValue());
							if (i == 0) {
								id_number = cell.getStringCellValue();
							}
							if (i == 1) {
								sticker_number = cell.getStringCellValue();
							}
							break;
						case Cell.CELL_TYPE_NUMERIC: // field that represents number cell type
							System.out.print(cell.getNumericCellValue());
							break;
						default:
						}
						i++;
					}
					j++;
					updateMachineQuery = "UPDATE public.machines SET sticker_number = '" + sticker_number + "' WHERE machine_id_number= '" + id_number + "';";
					db.executeQuery(updateMachineQuery);
					System.out.println("");
				}
				System.out.println(j);
				System.out.println("Kraj");
			} catch (Exception e) {
				e.printStackTrace();
			}

			/*
			 * Connection lConn = null; try { lConn = DriverManager.getConnection(urlL,
			 * user, password); } catch (SQLException e1) { // TODO Auto-generated catch
			 * block e1.printStackTrace(); } try { Statement stmnt = null; stmnt =
			 * lConn.createStatement(); ResultSet resultSet =
			 * stmnt.executeQuery(selectMachineQuery); if (resultSet != null) { while
			 * (resultSet.next()) { for (int i = 0; i < columns.length; i++) {
			 * db.executeQuery( "UPDATE public.machines SET  machine_id_number= '" +
			 * resultSet.getString(columns[i]) + "'	WHERE sticker_number = '" +
			 * resultSet.getString(columns[i]) + "';"); } } resultSet.close();
			 * stmnt.cancel(); lConn.close();
			 * 
			 * } else { lConn.close(); } } catch (SQLException e) { try { lConn.close(); }
			 * catch (SQLException e1) { // TODO Auto-generated catch block
			 * e1.printStackTrace(); } e.getMessage(); } catch (SecurityException e) { //
			 * TODO Auto-generated catch block e.printStackTrace(); } catch (IOException e)
			 * { // TODO Auto-generated catch block e.printStackTrace(); }
			 */

		}

	}
}
