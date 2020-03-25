package ie.ucd;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ie.ucd.objects.Student;
import ie.ucd.objects.Project;

public class ExcelWriter {
	public ExcelWriter() {
	}

	public void writeProjects(ArrayList<Project> projects, int numOfStudents) throws IOException, InvalidFormatException {
		String[] columns = { "Staff Name", "Research Activity", "Stream" };

		// Create a Workbook
		Workbook workbook = new XSSFWorkbook(); // new HSSFWorkbook() for generating `.xls` file

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
		for (Project project : projects) {
			Row row = sheet.createRow(rowNum++);

			row.createCell(0).setCellValue(project.getProposedBy());
			row.createCell(1).setCellValue(project.getResearchActivity());
			row.createCell(2).setCellValue(project.getStream());
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

			row.createCell(0).setCellValue(student.getFirstName());
			row.createCell(1).setCellValue(student.getLastName());
			row.createCell(2).setCellValue(student.getId());
			row.createCell(3).setCellValue(student.getStream());

			for (int i = 4, j = 0; i < 14; i++, j++) {
				row.createCell(i).setCellValue(student.getPreferenceList().get(j).getResearchActivity());
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

	public void writeAnalysis(Parser parser) throws IOException, InvalidFormatException {
		String[] columns = { "Research Activity", "Stream", "PDF", "Percentage Distribution" };

		// Create a Workbook
		Workbook workbook = new XSSFWorkbook(); // new HSSFWorkbook() for generating `.xls` file

		// Create a Sheet
		Sheet sheet = workbook.createSheet("Analysis");

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
		for (Project project : parser.someStaffsProjects) {
			Row row = sheet.createRow(rowNum++);

			row.createCell(0).setCellValue(project.getResearchActivity());
			row.createCell(1).setCellValue(project.getStream());
			row.createCell(2).setCellValue(project.getPreferredProbability());
			row.createCell(3)
					.setCellValue(parser.formatPercentage(project.getNumStudentsAssigned() / parser.totalProjectsAssigned * 100));
		}

		// Resize all columns to fit the content size
		for (int i = 0; i < columns.length; i++) {
			sheet.autoSizeColumn(i);
		}

		// Write the output to a file
		FileOutputStream fileOut = new FileOutputStream("Analysis" + parser.numberOfStudents + ".xlsx");
		workbook.write(fileOut);
		fileOut.close();

		// Closing the workbook
		workbook.close();
	}
}
