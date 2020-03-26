package ie.ucd.solvers;

import java.util.ArrayList;
import java.util.HashMap;
import ie.ucd.objects.Project;
import ie.ucd.objects.Student;

public class SimulatedAnnealing {
	private ArrayList<Project> projects;
	private HashMap<Integer, Student> students;

	public SimulatedAnnealing(ArrayList<Project> projects, HashMap<Integer, Student> students) {
		this.projects = projects;
		this.students = students;
	}

	private Double calculateGlobalSatisfaction() {
		Double satisfaction = 0.0;
		for (Student student : students.values()) {
			satisfaction += student.calculateSatisfaction();
		}
		return satisfaction;
	}

	// method check if projects equally distributed across supervisors.
	// if no, has cost.

	// method check if higher GPA means a greater chance of getting one's preferred projects.
	// add in student class?
}
