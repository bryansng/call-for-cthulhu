package ie.ucd.solvers;

import java.util.ArrayList;
import java.util.Random;
import ie.ucd.objects.Project;
import ie.ucd.objects.Student;

public class SimulatedAnnealing {
	public ArrayList<Student> run(ArrayList<Project> projects, ArrayList<Student> students) {
		double temperature = 100.0;
		double coolingRate = 0.003;
		double boltzmannConstant = 1.38064852e-23;
		int maxIteration = 999;
		// int maxIteration = projects.size();

		// keep track of students.
		// is that what we are changing?
		ArrayList<Student> currStudents = students;
		ArrayList<Student> nextStudents;
		ArrayList<Student> bestStudents = currStudents;

		// minimizing negative fitness == maximizing positive fitness?
		double currEnergy = calculateEnergy(currStudents);
		double nextEnergy;
		double bestEnergy = currEnergy;
		System.out.println("Starting energy: " + currEnergy);
		for (int i = 0; i < maxIteration; i++) {
			System.out.println("\nLoop " + i + " (temperature: " + temperature + ", bestEnergy: " + bestEnergy + "):");
			// random move to students to get new students.
			// depending on temperature,
			// if higher, make more risky random moves.
			// else, make more conservative moves.
			nextStudents = makeRandomMove(students);

			// compute new energy.
			nextEnergy = calculateEnergy(nextStudents);

			// decide if accept this new solution.
			double randomProbability = new Random().nextDouble();
			double absoluteEnergyDifference = Math.abs(nextEnergy - currEnergy);
			double acceptanceProbability = 1.0 - Math.exp(absoluteEnergyDifference / temperature);
			System.out.println("nextEnergy: " + nextEnergy);
			System.out.println("absoluteEnergyDifference: " + absoluteEnergyDifference);
			System.out.println("Acceptance Probability: " + acceptanceProbability);
			System.out.println("Random Probability: " + randomProbability);
			System.out.println("randomProbability <= acceptanceProbability: " + (randomProbability <= acceptanceProbability));
			if (randomProbability <= acceptanceProbability) {
				currStudents = nextStudents;
				System.out.println("New candidate solution accepted.");
			}

			// keep track of the best solution found, i.e. next lowest energy.
			if (nextEnergy < bestEnergy) {
				bestStudents = nextStudents;
				System.out.println("New best solution found.");
			}

			// cool system.
			temperature *= 1 - coolingRate;
		}
		return bestStudents;
	}

	// change project assigned between two students.
	private ArrayList<Student> makeRandomMove(ArrayList<Student> students) {
		ArrayList<Student> newStudents = clone(students);

		// get random two students.
		Random rand = new Random();
		int s1 = rand.nextInt(newStudents.size());
		int s2 = rand.nextInt(newStudents.size());
		while (s1 == s2) {
			s2 = rand.nextInt(newStudents.size());
		}

		// swap student assigned projects.
		Project p1 = newStudents.get(s1).getProjectAssigned(0);
		newStudents.get(s1).setProjectAssigned(newStudents.get(s2).getProjectAssigned(0), 0);
		newStudents.get(s2).setProjectAssigned(p1, 0);

		if (newStudents.get(s1).getStream().equals(newStudents.get(s2).getStream())) {
			System.out.println("Students have incompatible stream.");
		}

		return newStudents;
	}

	private ArrayList<Student> clone(ArrayList<Student> students) {
		ArrayList<Student> newStudents = new ArrayList<Student>();
		for (Student student : students) {
			newStudents.add(student.getCopy());
		}
		return newStudents;
	}

	private Double calculateEnergy(ArrayList<Student> students) {
		return 1.0 / calculateGlobalSatisfaction(students);
	}

	private Double calculateGlobalSatisfaction(ArrayList<Student> students) {
		Double satisfaction = 0.0;
		for (Student student : students) {
			satisfaction += student.calculateSatisfaction();
		}
		return satisfaction;
	}

	// method check if projects equally distributed across supervisors.
	// if no, has cost.

	// method check if higher GPA means a greater chance of getting one's preferred projects.
	// add in student class?
}
