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

	public void run() {
		double satisfaction = calculateGlobalSatisfaction();
		System.out.println("Satisfaction computed: " + satisfaction);

		// minimizing negative fitness == maximizing positive fitness?
		double startingTemperature = 100.0;
		double coolingRate = 0.003;
		int maxIteration = projects.size();

		// keep track of students.
		// is that what we are changing?

		double energy = satisfaction * -1;
		for (int i = 0; i < maxIteration; i++) {
			// random move to students to get new students.
			// depending on temperature,
			// if higher, make more risky random moves.
			// else, make more conservative moves.

			// compute new energy.

			// if new energy lower than curr energy, i.e. is better
			// then accept new random solution, i.e. keep track of this students
		}
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
