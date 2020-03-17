package ie.ucd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import ie.ucd.objects.Student;
import ie.ucd.objects.SupervisorProject;

public class Generator {
	Parser parser;
	Writer writer = new Writer();
	ArrayList<SupervisorProject> supervisorProject;
	HashMap<Integer, Student> students;

	public static void main(String[] args) throws IOException, InvalidFormatException {
		System.out.println("Hello World!\n\n");

		Parser parser;
		Writer writer = new Writer();
		ArrayList<SupervisorProject> supervisorProject;
		HashMap<Integer, Student> students;

		parser = new Parser();
		supervisorProject = parser.generateStaffProjects();
		for (SupervisorProject sp : supervisorProject) {
			System.out.println(sp);
		}
		System.out.println("\n\n");

		students = parser.generateStudents();
		for (Student s : students.values()) {
			System.out.println(s);
		}
		writer.writeSupervisorProjects(supervisorProject, 20);
		writer.writeStudents(students);

		generateRequiredFiles();
	}

	public static void generateRequiredFiles() throws IOException, InvalidFormatException {
		Parser parser;
		Writer writer = new Writer();
		ArrayList<SupervisorProject> supervisorProject;
		HashMap<Integer, Student> students;

		parser = new Parser(60);
		supervisorProject = parser.generateStaffProjects();
		students = parser.generateStudents();
		writer.writeSupervisorProjects(supervisorProject, 60);
		writer.writeStudents(students);
		System.out.println("Generating for 60 students...");

		parser = new Parser(120);
		supervisorProject = parser.generateStaffProjects();
		students = parser.generateStudents();
		writer.writeSupervisorProjects(supervisorProject, 120);
		writer.writeStudents(students);
		System.out.println("Generating for 120 students...");

		parser = new Parser(240);
		supervisorProject = parser.generateStaffProjects();
		students = parser.generateStudents();
		writer.writeSupervisorProjects(supervisorProject, 240);
		writer.writeStudents(students);
		System.out.println("Generating for 240 students...");

		parser = new Parser(500);
		supervisorProject = parser.generateStaffProjects();
		students = parser.generateStudents();
		writer.writeSupervisorProjects(supervisorProject, 500);
		writer.writeStudents(students);
		System.out.println("Generating for 500 students...");
		System.out.println("All done");
	}
}
