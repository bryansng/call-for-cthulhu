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

import ie.ucd.objects.Project;
import ie.ucd.objects.Staff;
import ie.ucd.objects.Student;
import ie.ucd.objects.SupervisorProject;

public class Parser {
	private ArrayList<Staff> allStaffsProjects = new ArrayList<Staff>();
	private ArrayList<SupervisorProject> someStaffsProjects = new ArrayList<SupervisorProject>();
	public ArrayList<String> allNames = new ArrayList<String>();
	HashMap<Integer, Student> studentMap = new HashMap<Integer, Student>();
	private int numberOfStudents;

	public Parser() {
		numberOfStudents = 20;
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

		int numAvgProjectsProposed = 3;
		int numStaffMembers = numberOfStudents / 2;
		for (int i = 0; i < numAvgProjectsProposed * numStaffMembers; i++) {
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
	private ArrayList<Staff> parseExcelFile() throws IOException {
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

	private ArrayList<String> parseNamesFile() throws IOException {
		String txtFile = "initials.txt";

		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(txtFile);
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));

		while (reader.ready()) {
			String line = reader.readLine();
			allNames.add(line);
		}
		reader.close();
		return allNames;
	}

	public HashMap<Integer, Student> generateStudents() throws IOException {
		parseNamesFile();

		HashMap<Integer, Student> studentMap = new HashMap<Integer, Student>();

		for (int i = 0; i < numberOfStudents; i++) {
			Integer randomInt = new Random().nextInt(allNames.size());
			String[] name = allNames.get(randomInt).split(",")[1].split(" ");
			String firstName = name[0];
			String lastName = "";
			if (name.length > 1) {
				lastName = name[name.length - 1];
			}

			Integer randomId = generateStudentId();
			while (studentMap.containsKey(randomId)) {
				randomId = generateStudentId();
			}
			String stream = generateStudentStream();
			studentMap.put(randomId, new Student(firstName, lastName, randomId, stream, generatePreferenceList(stream)));
		}
		return studentMap;
	}

	private Integer generateStudentId() {
		return new Random().nextInt(90000000) + 10000000; // 1000 0000 - 9999 9999.
	}

	private ArrayList<SupervisorProject> generatePreferenceList(String stream) {
		ArrayList<SupervisorProject> list = new ArrayList<SupervisorProject>();
		HashSet<Integer> usedIndex = new HashSet<Integer>();

		for (int i = 0; i < 10;) {
			int randomIndex = new Random().nextInt(someStaffsProjects.size());
			SupervisorProject aSP = someStaffsProjects.get(randomIndex);
			if (!usedIndex.contains(randomIndex) && aSP.hasCompatibleStream(stream)) {
				usedIndex.add(randomIndex);
				list.add(aSP);
				i++;
			}
		}

		return list;
	}

	private String generateStudentStream() {
		int randomInt = new Random().nextInt(5) + 1; // 1-5

		// 40% is DS, 2/5.
		if (randomInt == 4 || randomInt == 5) {
			return "DS";
		}
		return "CS"; // 60% is CS, 3/5.
	}
}
