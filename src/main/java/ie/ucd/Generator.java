package ie.ucd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import ie.ucd.objects.StaffMember;
import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import ie.ucd.objects.Student;
import ie.ucd.objects.Project;

public class Generator {
	public static void main(String[] args) throws Exception {
		System.out.println("Hello World!");
		generateRequiredFiles();
		readRequiredFiles();

		// playingNormalDistribution();
	}

	public static void generateRequiredFiles() throws IOException, InvalidFormatException {
		Parser parser;
		CSVFileWriter csvFileWriter = new CSVFileWriter();
		ExcelWriter writer = new ExcelWriter();
		ArrayList<Project> supervisorProject;
		HashMap<Integer, Student> students;

		System.out.println("Generating for 60 students...");
		parser = new Parser(60);
		supervisorProject = parser.generateStaffProjects();
		students = parser.generateStudents();
		writer.writeProjects(supervisorProject, 60);
		writer.writeStudents(students);
		writer.writeAnalysis(parser);
		System.out.println(parser.CSDSPercentage());
		csvFileWriter.writeProjects(supervisorProject, 60);
		csvFileWriter.writeStudents(students);
		csvFileWriter.writeAnalysis(parser);
		// System.out.println(parser.ProjectDistributionPercentage());
		// System.out.println(parser.Project1stPreferencePercentage());

		System.out.println("Generating for 120 students...");
		parser = new Parser(120);
		supervisorProject = parser.generateStaffProjects();
		students = parser.generateStudents();
		writer.writeProjects(supervisorProject, 120);
		writer.writeStudents(students);
		writer.writeAnalysis(parser);
		System.out.println(parser.CSDSPercentage());
		csvFileWriter.writeProjects(supervisorProject, 120);
		csvFileWriter.writeStudents(students);
		csvFileWriter.writeAnalysis(parser);
		// System.out.println(parser.ProjectDistributionPercentage());

		System.out.println("Generating for 240 students...");
		parser = new Parser(240);
		supervisorProject = parser.generateStaffProjects();
		students = parser.generateStudents();
		writer.writeProjects(supervisorProject, 240);
		writer.writeStudents(students);
		writer.writeAnalysis(parser);
		System.out.println(parser.CSDSPercentage());
		csvFileWriter.writeProjects(supervisorProject, 240);
		csvFileWriter.writeStudents(students);
		csvFileWriter.writeAnalysis(parser);
		// System.out.println(parser.ProjectDistributionPercentage());

		System.out.println("Generating for 500 students...");
		parser = new Parser(500);
		supervisorProject = parser.generateStaffProjects();
		students = parser.generateStudents();
		writer.writeProjects(supervisorProject, 500);
		writer.writeStudents(students);
		writer.writeAnalysis(parser);
		System.out.println(parser.CSDSPercentage());
		csvFileWriter.writeProjects(supervisorProject, 500);
		csvFileWriter.writeStudents(students);
		csvFileWriter.writeAnalysis(parser);
		// System.out.println(parser.ProjectDistributionPercentage());

		//generate staffMembers
		System.out.println("Generating StaffMembers.csv");
		ArrayList<StaffMember> staffMembers = parser.parseExcelFile();
		csvFileWriter.writeStaffMembers(staffMembers);
		System.out.println("All done\n");
	}

	public static void readRequiredFiles() throws Exception {
		CSVFileReader reader = new CSVFileReader();
		System.out.println("Reading the generated files now...");

		//reads all Project CSV files
		ArrayList<Project> projects_60 = reader.readProject("ProjectsCSV60.csv");
		System.out.println("Done: ProjectsCSV60.csv");
		ArrayList<Project> projects_120 = reader.readProject("ProjectsCSV120.csv");
		System.out.println("Done: ProjectsCSV120.csv");
		ArrayList<Project> projects_240 = reader.readProject("ProjectsCSV240.csv");
		System.out.println("Done: ProjectsCSV240.csv");
		ArrayList<Project> projects_500 = reader.readProject("ProjectsCSV500.csv");
		System.out.println("Done: ProjectsCSV500.csv");

//		for (Project project_60 : projects) System.out.println(project.toString());

		//reads Students from CSV files
		ArrayList<Student> students_60 = reader.readStudents("StudentsCSV60.csv", projects_60);
		System.out.println("Done: StudentsCSV60.csv");
		ArrayList<Student> students_120 = reader.readStudents("StudentsCSV120.csv", projects_120);
		System.out.println("Done: StudentsCSV120.csv");
		ArrayList<Student> students_240 = reader.readStudents("StudentsCSV240.csv", projects_240);
		System.out.println("Done: StudentsCSV240.csv");
		ArrayList<Student> students_500 = reader.readStudents("StudentsCSV500.csv", projects_500);
		System.out.println("Done: StudentsCSV500.csv");

//		for (Student student : students_500) System.out.println(student.toString());

		//read staffMembers from CSV file
		ArrayList<StaffMember> staffMembers = reader.readStaffMembers("StaffMembersCSV.csv");

//		for(StaffMember staffMember : staffMembers) System.out.println(staffMember.toString());
	}

	public static void playingNormalDistribution() {
		Random rand = new Random();
		Double maxx = Double.NEGATIVE_INFINITY;
		Double minn = Double.POSITIVE_INFINITY;

		// why use TreeMap, tldr; entries are sorted automatically:
		// https://stackoverflow.com/questions/571388/how-can-i-sort-the-keys-of-a-map-in-java
		Map<Double, Double> hm = new TreeMap<Double, Double>();
		for (int i = 0; i < 100; i++) {
			double aDouble = rand.nextGaussian();
			while (isOutlier(aDouble)) {
				aDouble = rand.nextGaussian();
			}

			if (aDouble > maxx) {
				maxx = aDouble;
			}
			if (aDouble < minn) {
				minn = aDouble;
			}

			// String formattedDouble = String.format("%.2f", aDouble);
			// aDouble = Double.parseDouble(formattedDouble);
			if (!hm.containsKey(aDouble)) {
				hm.put(aDouble, 0.0);
			}
			hm.replace(aDouble, hm.get(aDouble) + 1.0);
		}
		for (Map.Entry<Double, Double> entry : hm.entrySet()) {
			System.out.println(entry.getKey() + " - " + entry.getValue());
		}
		System.out.println("Min: " + minn);
		System.out.println("Max: " + maxx);

		// normalize distribution to be from 0 to 100.
		// minn = Math.abs(minn);
		// maxx += minn;
		// for (Double key : hm.keySet()) {
		// 	hm.replace(key, ((key + minn) / maxx) * 100);
		// }

		// for (Map.Entry<Double, Double> entry : hm.entrySet()) {
		// 	System.out.println(entry.getKey() + " - " + entry.getValue());
		// }

		AbstractRealDistribution distr = new NormalDistribution();
		for (Map.Entry<Double, Double> entry : hm.entrySet()) {
			System.out.println(entry.getKey() + " - " + distr.density(entry.getKey()));
		}
	}

	public static boolean isOutlier(double value) {
		return !(value >= -2.0 && value <= 2.0);
	}
}
