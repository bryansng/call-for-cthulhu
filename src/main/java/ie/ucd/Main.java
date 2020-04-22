package ie.ucd;

import java.io.IOException;
import java.util.ArrayList;

import ie.ucd.objects.StaffMember;
import ie.ucd.solvers.GeneticAlgorithm;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import ie.ucd.objects.Student;
import ie.ucd.solvers.SimulatedAnnealing;
import ie.ucd.ui.solver.SolverPane;
import ie.ucd.solvers.Solver;
import ie.ucd.Common.SolverType;
import ie.ucd.io.CSVFileReader;
import ie.ucd.io.CSVFileWriter;
import ie.ucd.io.ExcelWriter;
import ie.ucd.io.Parser;
import ie.ucd.objects.CandidateSolution;
import ie.ucd.objects.Project;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	public SolverPane solverPane;

	public static void main(String[] args) throws IOException, InvalidFormatException, Exception {
		System.out.println("Running application...\n");
		// generateExcelFiles();
		// generateCSVFiles();
		// readCSVFiles();
		// readTest();
		// applySimulatedAnnealing();
		// applyGeneticAlgorithm();
		launch();
	}

	@Override
	public void start(Stage stage) throws IOException, InvalidFormatException, InterruptedException {
		System.out.println("Generating for 500 students...");

		solverPane = new SolverPane(stage, SolverType.GeneticAlgorithm);
		// solverPane = new SolverPane(stage, SolverType.SimulatedAnnealing);
		Scene scene = new Scene(solverPane, 1280, 600);
		scene.getStylesheets().add("ui/solver/constraints.css");

		stage.setScene(scene);
		stage.show();

		System.out.println("All done");
	}

	public static void applyGeneticAlgorithm() throws IOException {
		System.out.println("Generating for 500 students...");

		Parser parser = new Parser();
		CandidateSolution solution = new CandidateSolution(500, parser.getStaffMembers(), parser.getNames(), null, null);
		solution.generateProjects();
		solution.generateStudents();
		System.out.println(solution.CSDSPercentage());

		Solver solver = new GeneticAlgorithm(solution, null);
		solver.run();
		CandidateSolution bestSolution = solver.getBestSolution();
		// int count = 1;
		// for (Student student : bestSolution.getStudents()) {
		// 	System.out.println(count++ + " : " + student.getId() + " : " + student.getFirstName() + " "
		// 			+ student.getLastName() + " : " + student.getProjectAssigned(0).getResearchActivity());
		// }
		// System.out.println("FITNESS OF FINAL SOLUTION: " + solver.getFinalSolutionFitness());

		System.out.println("All done");
	}

	public static void applySimulatedAnnealing() throws IOException, InvalidFormatException {
		System.out.println("Generating for 500 students...");

		Parser parser = new Parser();
		CandidateSolution solution = new CandidateSolution(500, parser.getStaffMembers(), parser.getNames(), null, null);
		solution.generateProjects();
		solution.generateStudents();
		System.out.println(solution.CSDSPercentage());

		Solver solver = new SimulatedAnnealing(solution, null);
		solver.run();
		CandidateSolution bestSolution = solver.getBestSolution();

		System.out.println("All done");
	}

	public static void generateExcelFiles() throws IOException, InvalidFormatException {
		Parser parser = new Parser();
		ExcelWriter writer = new ExcelWriter();
		ArrayList<Project> projects;
		ArrayList<Student> students;

		System.out.println("Generating for 500 students...");
		CandidateSolution solution = new CandidateSolution(500, parser.getStaffMembers(), parser.getNames(), null, null);
		projects = solution.generateProjects();
		students = solution.generateStudents();
		writer.writeProjects(projects, 500);
		writer.writeStudents(students);
		writer.writeAnalysis(solution);
		System.out.println(solution.CSDSPercentage());
		// System.out.println(parser.ProjectDistributionPercentage());

		System.out.println("All done\n");
	}

	public static void generateCSVFiles() throws IOException, InvalidFormatException {
		Parser parser = new Parser();
		CSVFileWriter csvFileWriter = new CSVFileWriter();
		ArrayList<Project> projects;
		ArrayList<Student> students;

		System.out.println("Generating for 500 students...");
		CandidateSolution solution = new CandidateSolution(500, parser.getStaffMembers(), parser.getNames(), null, null);
		projects = solution.generateProjects();
		students = solution.generateStudents();
		System.out.println(solution.CSDSPercentage());
		csvFileWriter.writeProjects(projects);
		csvFileWriter.writeStudents(students);
		// csvFileWriter.writeAnalysis(solution);

		// generate staffMembers
		System.out.println("Generating StaffMembers.csv");
		ArrayList<StaffMember> staffMembers = parser.getStaffMembers();
		csvFileWriter.writeStaffMembers(staffMembers);
		System.out.println("All done\n");
	}

	public static void readTest() throws Exception {
		Parser parser = new Parser();
		CSVFileReader reader = new CSVFileReader();
		ArrayList<Project> projects = reader.readProject("CSVs/ProjectsCSV100.csv", parser.getStaffMembersMap());
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
		ArrayList<Project> projects_60 = reader.readProject("CSVs/ProjectsCSV60.csv", parser.getStaffMembersMap());
		System.out.println("Done: ProjectsCSV60.csv");
		ArrayList<Project> projects_120 = reader.readProject("CSVs/ProjectsCSV120.csv", parser.getStaffMembersMap());
		System.out.println("Done: ProjectsCSV120.csv");
		ArrayList<Project> projects_240 = reader.readProject("CSVs/ProjectsCSV240.csv", parser.getStaffMembersMap());
		System.out.println("Done: ProjectsCSV240.csv");
		ArrayList<Project> projects_500 = reader.readProject("CSVs/ProjectsCSV500.csv", parser.getStaffMembersMap());
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
