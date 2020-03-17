package ie.ucd;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Parser {
	// https://stackoverflow.com/questions/51259388/read-data-from-excel-in-java
	public HashMap<String, Project> parseProjects() throws IOException {
		String excelFile = "MiskatonicStaffMembers.xlsx";

		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(excelFile);

		XSSFWorkbook workbook = new XSSFWorkbook(is);
		XSSFSheet spreadsheet = workbook.getSheetAt(0);
		Iterator<Row> rowIterator = spreadsheet.iterator();

		HashMap<String, Project> hm = new HashMap<String, Project>();
		while (rowIterator.hasNext()) {
			XSSFRow row = (XSSFRow) rowIterator.next();

			String proposedBy = row.getCell(0).getStringCellValue();
			String researchActivity = row.getCell(1).getStringCellValue();
			String researchAreas = row.getCell(2).getStringCellValue();
			String specialFocus = "";
			// String specialFocus = row.getCell(3).getStringCellValue();
			hm.put(researchActivity, new Project(proposedBy, researchActivity, researchAreas, specialFocus));

			// Iterator<Cell> cellIterator = row.cellIterator();

			// while (cellIterator.hasNext()) {
			// 	Cell cell = cellIterator.next();

			// 	switch (cell.getCellType()) {
			// 		case NUMERIC:
			// 			System.out.print(cell.getNumericCellValue() + " \t\t ");
			// 			break;
			// 		case STRING:
			// 			System.out.print(cell.getStringCellValue() + " \t\t ");
			// 			break;
			// 		default:
			// 	}
			// }
			// System.out.println();
		}
		is.close();
		workbook.close();

		return hm;
	}
}
