package ie.ucd.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import ie.ucd.Common;
import ie.ucd.Settings;
import ie.ucd.objects.StaffMember;

public class Parser {
	public ArrayList<StaffMember> staffMembers = new ArrayList<StaffMember>();
	public HashMap<String, StaffMember> staffMembersMap = new HashMap<String, StaffMember>();
	public ArrayList<String> names = new ArrayList<String>();

	public Parser() {
		try {
			parseExcelFile();
			parseNamesFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<StaffMember> getStaffMembers() {
		return staffMembers;
	}

	public HashMap<String, StaffMember> getStaffMembersMap() {
		return staffMembersMap;
	}

	public ArrayList<String> getNames() {
		return names;
	}

	// 30, 60, 120, 250 staffs.
	// https://stackoverflow.com/questions/51259388/read-data-from-excel-in-java
	public ArrayList<StaffMember> parseExcelFile() throws IOException {
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
				if (Common.DEBUG_IO_UNICODE && proposedBy.startsWith("Pep"))
					System.out.println(proposedBy);

				StaffMember staffMember = new StaffMember(proposedBy, researchActivity, researchAreas, specialFocus);
				staffMembers.add(staffMember);
				staffMembersMap.put(proposedBy, staffMember);

				if (Common.DEBUG_IO_UNICODE && proposedBy.startsWith("Pep"))
					System.out.println(staffMember);

				if (Common.DEBUG_IO_UNICODE && proposedBy.startsWith("Pep"))
					System.out.println(staffMembersMap.containsKey(proposedBy));
			}
		}
		is.close();
		workbook.close();
		// System.out.println(calculateMaxUniqueProjects()); // 1115, 1115 / avgProposedProjects * 2 = 743.
		Settings.recomputeMaxStudents(calculateMaxUniqueProjects());
		return staffMembers;
	}

	private Integer calculateMaxUniqueProjects() {
		HashSet<String> projectSet = new HashSet<>();
		for (StaffMember sm : staffMembers) {
			for (String project : sm.getResearchActivities()) {
				projectSet.add(project);
			}
		}
		return projectSet.size();
	}

	private ArrayList<String> parseNamesFile() throws IOException {
		String txtFile = "initials.txt";

		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(txtFile);
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));

		while (reader.ready()) {
			String line = reader.readLine();
			names.add(line);
		}
		reader.close();
		return names;
	}
}
