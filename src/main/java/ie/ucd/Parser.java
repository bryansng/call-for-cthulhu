package ie.ucd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ie.ucd.objects.StaffMember;
import ie.ucd.objects.Student;
import ie.ucd.objects.Project;

public class Parser {
	public ArrayList<StaffMember> allStaffsProjects = new ArrayList<StaffMember>();
	public ArrayList<Project> someStaffsProjects = new ArrayList<Project>();
	public ArrayList<String> allNames = new ArrayList<String>();
	public int numberOfStudents;

	public double numDSStudents = 0.0;
	public double numCSStudents = 0.0;
	public double totalProjectsAssigned = 0.0;

	public Parser() {
		numberOfStudents = 20;
	}

	public Parser(int numberOfStudents) {
		this.numberOfStudents = numberOfStudents;
	}

	public ArrayList<Project> generateStaffProjects() throws IOException {
		parseExcelFile();

		int numStaffMembers = numberOfStudents / 2;
		for (int i = 0; i < Common.numAvgProjectsProposed * numStaffMembers; i++) {
			int randInt = new Random().nextInt(allStaffsProjects.size());
			while (allStaffsProjects.get(randInt).isAllActivitiesUsed()) {
				randInt = new Random().nextInt(allStaffsProjects.size());
			}

			someStaffsProjects.add(allStaffsProjects.get(randInt).getProject());
		}

		return someStaffsProjects;
	}

	// 30, 60, 120, 250 staffs.
	// https://stackoverflow.com/questions/51259388/read-data-from-excel-in-java
	private ArrayList<StaffMember> parseExcelFile() throws IOException {
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

			if (!researchActivity.equals("")) {
				allStaffsProjects.add(new StaffMember(proposedBy, researchActivity, researchAreas, specialFocus));
			}
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

	public ArrayList<Student> generateStudents() throws IOException {
		parseNamesFile();

		HashSet<Integer> usedStudentIDs = new HashSet<Integer>();
		ArrayList<Student> students = new ArrayList<Student>();

		for (int i = 0; i < numberOfStudents; i++) {
			Integer randomInt = new Random().nextInt(allNames.size());
			String[] name = allNames.get(randomInt).split(",")[1].split(" ");
			String firstName = name[0];
			String lastName = "";
			if (name.length > 1) {
				lastName = name[name.length - 1];
			}

			Integer randomId = generateStudentId();
			while (usedStudentIDs.contains(randomId)) {
				randomId = generateStudentId();
			}
			String stream = generateStudentStream();
			usedStudentIDs.add(randomId);
			students.add(new Student(firstName, lastName, randomId, stream, 0.0, generatePreferenceList(stream, randomId)));
		}
		return students;
	}

	private Integer generateStudentId() {
		return new Random().nextInt(90000000) + 10000000; // 1000 0000 - 9999 9999.
	}

	private ArrayList<Project> generatePreferenceList(String stream, Integer randomId) {
		ArrayList<Project> list = new ArrayList<Project>();
		HashSet<Integer> usedIndex = new HashSet<Integer>();

		// assuming impossible to run out of projects to give as 1st preference since someStaffsProjects.size() always > numOfStudents.
		for (int i = 0; i < 10;) {
			int randomIndex = new Random().nextInt(someStaffsProjects.size());
			Project project = someStaffsProjects.get(randomIndex);
			if (!usedIndex.contains(randomIndex) && project.hasCompatibleStream(stream) && project.doesStudentPreferProject()
					&& (i != 0 || (!project.isGivenAs1stPreference() && i == 0))) {
				project.incrementAsPreference();
				if (i == 0) {
					project.incrementAs1stPreference();
					project.incrementStudentsAssigned();
					project.setIsGivenAs1stPreference(true);
				}

				usedIndex.add(randomIndex);
				list.add(project);
				i++;
				totalProjectsAssigned++;
			}
		}

		return list;
	}

	private String generateStudentStream() {
		int randomInt = new Random().nextInt(5) + 1; // 1-5

		// 40% is DS, 2/5.
		if (randomInt == 4 || randomInt == 5) {
			numDSStudents += 1.0;
			return "DS";
		}
		numCSStudents += 1.0;
		return "CS"; // 60% is CS, 3/5.
	}

	public String CSDSPercentage() {
		return "Percentage CS: " + formatPercentage(numCSStudents / numberOfStudents * 100.0) + "\nPercentage DS: "
				+ formatPercentage(numDSStudents / numberOfStudents * 100.0) + "\n";
	}

	public String ProjectDistributionPercentage() {
		String res = "\n\nPercentage project distribution:\n";
		for (Project project : someStaffsProjects) {
			res += project.getResearchActivity() + " - " + project.getStream() + " - " + project.getPreferredProbability()
					+ " - " + formatPercentage(project.getNumAsPreference() / totalProjectsAssigned * 100) + "\n";
		}
		return res;
	}

	public String Project1stPreferencePercentage() {
		String res = "\n\nPercentage project as 1st Preference:\n";
		for (Project project : someStaffsProjects) {
			res += project.getResearchActivity() + " - " + project.getStream() + " - " + project.getPreferredProbability()
					+ " - " + formatPercentage(project.getNumAs1stPreference() / numberOfStudents * 100) + "\n";
		}
		return res;
	}

	public String formatPercentage(double percentage) {
		return String.format("%.2f%%", percentage);
	}
}
