package ie.ucd;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ie.ucd.objects.Student;
import ie.ucd.objects.SupervisorProject;

public class Writer {
	public Writer() {
	}

	public void writeSupervisorProjects(ArrayList<SupervisorProject> staffs, int numOfStudents)
			throws IOException, InvalidFormatException {
		String[] columns = { "Staff Name", "Research Activity", "Stream" };

		// Create a Workbook
		Workbook workbook = new XSSFWorkbook(); // new HSSFWorkbook() for generating `.xls` file

		/* CreationHelper helps us create instances of various things like DataFormat, Hyperlink, RichTextString etc, in a format (HSSF, XSSF) independent way */
		CreationHelper createHelper = workbook.getCreationHelper();

		// Create a Sheet
		Sheet sheet = workbook.createSheet("Supervisors and their Projects");

		// Create a Font for styling header cells
		Font headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerFont.setFontHeightInPoints((short) 14);
		headerFont.setColor(IndexedColors.RED.getIndex());

		// Create a CellStyle with the font
		CellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.setFont(headerFont);

		// Create a Row
		Row headerRow = sheet.createRow(0);

		// Create cells
		for (int i = 0; i < columns.length; i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(columns[i]);
			cell.setCellStyle(headerCellStyle);
		}

		// Create Other rows and cells with employees data
		int rowNum = 1;
		for (SupervisorProject sp : staffs) {
			Row row = sheet.createRow(rowNum++);

			row.createCell(0).setCellValue(sp.proposedBy);
			row.createCell(1).setCellValue(sp.researchActivity);
			row.createCell(2).setCellValue(sp.specialFocus);
		}

		// Resize all columns to fit the content size
		for (int i = 0; i < columns.length; i++) {
			sheet.autoSizeColumn(i);
		}

		// Write the output to a file
		FileOutputStream fileOut = new FileOutputStream("StaffProject" + numOfStudents + ".xlsx");
		workbook.write(fileOut);
		fileOut.close();

		// Closing the workbook
		workbook.close();
	}

	// https://www.callicoder.com/java-write-excel-file-apache-poi/
	public void writeStudents(HashMap<Integer, Student> students) throws IOException, InvalidFormatException {
		String[] columns = { "First Name", "Last Name", "ID", "Stream", "Preference 1", "Preference 2", "Preference 3",
				"Preference 4", "Preference 5", "Preference 6", "Preference 7", "Preference 8", "Preference 9",
				"Preference 10" };

		// Create a Workbook
		Workbook workbook = new XSSFWorkbook(); // new HSSFWorkbook() for generating `.xls` file

		/* CreationHelper helps us create instances of various things like DataFormat, Hyperlink, RichTextString etc, in a format (HSSF, XSSF) independent way */
		CreationHelper createHelper = workbook.getCreationHelper();

		// Create a Sheet
		Sheet sheet = workbook.createSheet("Students");

		// Create a Font for styling header cells
		Font headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerFont.setFontHeightInPoints((short) 14);
		headerFont.setColor(IndexedColors.RED.getIndex());

		// Create a CellStyle with the font
		CellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.setFont(headerFont);

		// Create a Row
		Row headerRow = sheet.createRow(0);

		// Create cells
		for (int i = 0; i < columns.length; i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(columns[i]);
			cell.setCellStyle(headerCellStyle);
		}

		// Create Other rows and cells with employees data
		int rowNum = 1;
		Collection<Student> studentsArray = students.values();
		for (Student student : studentsArray) {
			Row row = sheet.createRow(rowNum++);

			row.createCell(0).setCellValue(student.firstName);
			row.createCell(1).setCellValue(student.lastName);
			row.createCell(2).setCellValue(student.id);
			row.createCell(3).setCellValue(student.stream);

			for (int i = 4, j = 0; i < 14; i++, j++) {
				row.createCell(i).setCellValue(student.preferenceList.get(j).researchActivity);
			}
		}

		// Resize all columns to fit the content size
		for (int i = 0; i < columns.length; i++) {
			sheet.autoSizeColumn(i);
		}

		// Write the output to a file
		FileOutputStream fileOut = new FileOutputStream("StudentPreference" + students.size() + ".xlsx");
		workbook.write(fileOut);
		fileOut.close();

		// Closing the workbook
		workbook.close();
	}
}
