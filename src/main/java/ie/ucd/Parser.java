package ie.ucd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Parser {
	private ArrayList<Staff> allStaffsProjects = new ArrayList<Staff>();
	private ArrayList<SupervisorProject> someStaffsProjects = new ArrayList<SupervisorProject>();
	private int numberOfStudents;

	public Parser() {
		numberOfStudents = 60;
	}

	public Parser(int numberOfStudents) {
		this.numberOfStudents = numberOfStudents;
	}

	public ArrayList<Project> generateStaffProjectsV2() throws IOException {
		ArrayList<String> staffNames = new ArrayList<String>();
		ArrayList<Project> projectArray = new ArrayList<Project>();
		String excelFile = "MiskatonicStaffMembers.xlsx";

		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(excelFile);

		XSSFWorkbook workbook = new XSSFWorkbook(is);
		XSSFSheet spreadsheet = workbook.getSheetAt(0);
		Iterator<Row> rowIterator = spreadsheet.iterator();

		HashSet<String> projectSet = new HashSet<String>();
		HashSet<String> staffNameSet = new HashSet<String>();
		while (rowIterator.hasNext()) {
			XSSFRow row = (XSSFRow) rowIterator.next();

			String proposedBy = row.getCell(0) == null ? "" : row.getCell(0).getStringCellValue();
			if (!staffNameSet.contains(proposedBy)) {
				staffNameSet.add(proposedBy);
				staffNames.add(proposedBy);
			}

			String[] researchActivities = row.getCell(1) == null ? new String[] {}
					: row.getCell(1).getStringCellValue().split(", ");
			String specialFocus = row.getCell(3) == null ? "" : row.getCell(3).getStringCellValue();

			for (String researchActivity : researchActivities) {
				if (!projectSet.contains(researchActivity)) {
					projectSet.add(researchActivity);
					projectArray.add(new Project(researchActivity, specialFocus));
				}
			}
		}
		is.close();
		workbook.close();

		return projectArray;
	}

	public ArrayList<SupervisorProject> generateStaffProjects() throws IOException {
		parseExcelFile();

		for (int i = 0; i < numberOfStudents / 2; i++) {
			int randInt = new Random().nextInt(allStaffsProjects.size());
			while (allStaffsProjects.get(randInt).isAllActivitiesUsed()) {
				randInt = new Random().nextInt(allStaffsProjects.size());
			}

			someStaffsProjects.add(allStaffsProjects.get(randInt).getSupervisorProject());
		}

		return someStaffsProjects;
	}

	// 30, 60, 120, 250 staffs.
	// https://stackoverflow.com/questions/51259388/read-data-from-excel-in-java
	public ArrayList<Staff> parseExcelFile() throws IOException {
		String excelFile = "MiskatonicStaffMembers.xlsx";

		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(excelFile);

		XSSFWorkbook workbook = new XSSFWorkbook(is);
		XSSFSheet spreadsheet = workbook.getSheetAt(0);
		Iterator<Row> rowIterator = spreadsheet.iterator();

		while (rowIterator.hasNext()) {
			XSSFRow row = (XSSFRow) rowIterator.next();

			String proposedBy = row.getCell(0) == null ? "" : row.getCell(0).getStringCellValue();
			String researchActivity = row.getCell(1) == null ? "" : row.getCell(1).getStringCellValue();
			String researchAreas = row.getCell(2) == null ? "" : row.getCell(2).getStringCellValue();
			String specialFocus = row.getCell(3) == null ? "" : row.getCell(3).getStringCellValue();

			allStaffsProjects.add(new Staff(proposedBy, researchActivity, researchAreas, specialFocus));
		}
		is.close();
		workbook.close();

		return allStaffsProjects;
	}

	public HashMap<Integer, Student> generateStudents() throws IOException {
		HashMap<Integer, Student> studentMap = new HashMap<Integer, Student>();
		String txtFile = "initials.txt";

		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(txtFile);
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));

		while (reader.ready()) {
			String line = reader.readLine();
			String[] names = line.split(",")[2].split(" ");
			String firstName = names[0];
			String lastName = "";
			if (names.length > 1) {
				lastName = names[names.length - 1];
			}

			Integer randomId = generateStudentId();
			while (studentMap.containsKey(randomId)) {
				randomId = generateStudentId();
			}
			studentMap.put(randomId, new Student(firstName, lastName, randomId, generatePreferenceList()));
		}

		return studentMap;
	}

	private Integer generateStudentId() {
		return new Random().nextInt(90000000) + 10000000; // 1000 0000 - 9999 9999.
	}

	private ArrayList<SupervisorProject> generatePreferenceList() {
		ArrayList<SupervisorProject> list = new ArrayList<SupervisorProject>();
		HashSet<Integer> usedIndex = new HashSet<Integer>();

		for (int i = 0; i < 10;) {
			int randomIndex = new Random().nextInt(list.size());
			if (!usedIndex.contains(randomIndex)) {
				usedIndex.add(randomIndex);
				list.add(someStaffsProjects.get(randomIndex));
				i++;
			}
		}

		return list;
	}
}
