package ie.ucd;

import java.io.IOException;
import java.util.ArrayList;

import ie.ucd.objects.StaffMember;
import ie.ucd.solvers.GeneticAlgorithm;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import ie.ucd.objects.Student;
import ie.ucd.solvers.SimulatedAnnealing;
import ie.ucd.io.CSVFileReader;
import ie.ucd.io.CSVFileWriter;
import ie.ucd.io.ExcelWriter;
import ie.ucd.io.Parser;
import ie.ucd.objects.Project;

public class Generator {
	public static void main(String[] args) throws IOException, InvalidFormatException, Exception {
		System.out.println("Running application...\n");

		Parser parser = new Parser(200);
		ArrayList<Project> projects = parser.generateStaffProjects();
		ArrayList<Student> students = parser.generateStudents();

		GeneticAlgorithm GA = new GeneticAlgorithm();
		GA.run(students, projects);
		students = GA.getFittestStudentSolution();
		int count = 1;
		for (Student student : students) {
			System.out.println(count + " : " + student.getId() + " : " + student.getProjectAssigned(0).getResearchActivity());
			count++;
		}
		// generateExcelFiles();
		// generateCSVFiles();
		// readCSVFiles();
		// readTest();
		// applySimulatedAnnealing();

		// playingNormalDistribution();
	}

	public static void applySimulatedAnnealing() throws IOException, InvalidFormatException {
		Parser parser;
		ArrayList<Project> projects;
		ArrayList<Student> students;

		System.out.println("Generating for 500 students...");
		parser = new Parser(500);
		projects = parser.generateStaffProjects();
		students = parser.generateStudents();
		System.out.println(parser.CSDSPercentage());
		students = new SimulatedAnnealing().run(projects, students);
		System.out.println("All done");
	}

	public static void generateExcelFiles() throws IOException, InvalidFormatException {
		Parser parser;
		ExcelWriter writer = new ExcelWriter();
		ArrayList<Project> projects;
		ArrayList<Student> students;

		System.out.println("Generating for 60 students...");
		parser = new Parser(60);
		projects = parser.generateStaffProjects();
		students = parser.generateStudents();
		writer.writeProjects(projects, 60);
		writer.writeStudents(students);
		writer.writeAnalysis(parser);
		System.out.println(parser.CSDSPercentage());
		// System.out.println(parser.ProjectDistributionPercentage());
		// System.out.println(parser.Project1stPreferencePercentage());

		System.out.println("Generating for 120 students...");
		parser = new Parser(120);
		projects = parser.generateStaffProjects();
		students = parser.generateStudents();
		writer.writeProjects(projects, 120);
		writer.writeStudents(students);
		writer.writeAnalysis(parser);
		System.out.println(parser.CSDSPercentage());
		// System.out.println(parser.ProjectDistributionPercentage());

		System.out.println("Generating for 240 students...");
		parser = new Parser(240);
		projects = parser.generateStaffProjects();
		students = parser.generateStudents();
		writer.writeProjects(projects, 240);
		writer.writeStudents(students);
		writer.writeAnalysis(parser);
		System.out.println(parser.CSDSPercentage());
		// System.out.println(parser.ProjectDistributionPercentage());

		System.out.println("Generating for 500 students...");
		parser = new Parser(500);
		projects = parser.generateStaffProjects();
		students = parser.generateStudents();
		writer.writeProjects(projects, 500);
		writer.writeStudents(students);
		writer.writeAnalysis(parser);
		System.out.println(parser.CSDSPercentage());
		// System.out.println(parser.ProjectDistributionPercentage());

		System.out.println("All done\n");
	}

	public static void generateCSVFiles() throws IOException, InvalidFormatException {
		Parser parser;
		CSVFileWriter csvFileWriter = new CSVFileWriter();
		ArrayList<Project> projects;
		ArrayList<Student> students;

		System.out.println("Generating for 60 students...");
		parser = new Parser(60);
		projects = parser.generateStaffProjects();
		students = parser.generateStudents();
		System.out.println(parser.CSDSPercentage());
		csvFileWriter.writeProjects(projects, 60);
		csvFileWriter.writeStudents(students);
		// csvFileWriter.writeAnalysis(parser);

		System.out.println("Generating for 120 students...");
		parser = new Parser(120);
		projects = parser.generateStaffProjects();
		students = parser.generateStudents();
		System.out.println(parser.CSDSPercentage());
		csvFileWriter.writeProjects(projects, 120);
		csvFileWriter.writeStudents(students);
		// csvFileWriter.writeAnalysis(parser);

		System.out.println("Generating for 240 students...");
		parser = new Parser(240);
		projects = parser.generateStaffProjects();
		students = parser.generateStudents();
		System.out.println(parser.CSDSPercentage());
		csvFileWriter.writeProjects(projects, 240);
		csvFileWriter.writeStudents(students);
		// csvFileWriter.writeAnalysis(parser);

		System.out.println("Generating for 500 students...");
		parser = new Parser(500);
		projects = parser.generateStaffProjects();
		students = parser.generateStudents();
		System.out.println(parser.CSDSPercentage());
		csvFileWriter.writeProjects(projects, 500);
		csvFileWriter.writeStudents(students);
		// csvFileWriter.writeAnalysis(parser);

		// generate staffMembers
		System.out.println("Generating StaffMembers.csv");
		ArrayList<StaffMember> staffMembers = parser.allStaffsProjects;
		csvFileWriter.writeStaffMembers(staffMembers);
		System.out.println("All done\n");
	}

	public static void readTest() throws Exception {
		Parser parser = new Parser();
		CSVFileReader reader = new CSVFileReader();
		ArrayList<Project> projects = reader.readProject("CSVs/ProjectsCSV100.csv", parser.allStaffMembers);
		ArrayList<Student> students = reader.readStudents("CSVs/StudentsCSV100.csv", projects);
		for (Student s : students) {
			System.out.println(s);
		}
		System.out.println("Done: StudentsCSV100.csv");
	}

	public static void readCSVFiles() throws Exception {
		Parser parser = new Parser();
		CSVFileReader reader = new CSVFileReader();
		System.out.println("Reading generated files...");

		// reads all Project CSV files
		ArrayList<Project> projects_60 = reader.readProject("CSVs/ProjectsCSV60.csv", parser.allStaffMembers);
		System.out.println("Done: ProjectsCSV60.csv");
		ArrayList<Project> projects_120 = reader.readProject("CSVs/ProjectsCSV120.csv", parser.allStaffMembers);
		System.out.println("Done: ProjectsCSV120.csv");
		ArrayList<Project> projects_240 = reader.readProject("CSVs/ProjectsCSV240.csv", parser.allStaffMembers);
		System.out.println("Done: ProjectsCSV240.csv");
		ArrayList<Project> projects_500 = reader.readProject("CSVs/ProjectsCSV500.csv", parser.allStaffMembers);
		System.out.println("Done: ProjectsCSV500.csv");

		// for (Project project : projects_500)
		// 	System.out.println(project.toString());

		// reads Students from CSV files
		ArrayList<Student> students_60 = reader.readStudents("CSVs/StudentsCSV60.csv", projects_60);
		System.out.println("Done: StudentsCSV60.csv");
		ArrayList<Student> students_120 = reader.readStudents("CSVs/StudentsCSV120.csv", projects_120);
		System.out.println("Done: StudentsCSV120.csv");
		ArrayList<Student> students_240 = reader.readStudents("CSVs/StudentsCSV240.csv", projects_240);
		System.out.println("Done: StudentsCSV240.csv");
		ArrayList<Student> students_500 = reader.readStudents("CSVs/StudentsCSV500.csv", projects_500);
		System.out.println("Done: StudentsCSV500.csv");

		// for (Student student : students_500)
		// 	System.out.println(student.toString());

		// read staffMembers from CSV file
		ArrayList<StaffMember> staffMembers = reader.readStaffMembers("CSVs/StaffMembersCSV.csv");

		// for (StaffMember staffMember : staffMembers)
		// 	System.out.println(staffMember.toString());
		System.out.println("All done\n");
	}
}
