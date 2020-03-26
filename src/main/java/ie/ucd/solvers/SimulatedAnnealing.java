package ie.ucd.solvers;

import java.util.ArrayList;
import java.util.Random;

import ie.ucd.Common;
import ie.ucd.objects.Project;
import ie.ucd.objects.Student;

public class SimulatedAnnealing {
	double storedSatisfaction;

	public ArrayList<Student> run(ArrayList<Project> projects, ArrayList<Student> students) {
		double temperature = Common.SHOW_SA_DEBUG ? 100 : 1000.0; // 100
		double coolingRate = 0.003;
		double boltzmannConstant = 1.38064852e-23;
		int maxIteration = Common.SHOW_SA_DEBUG ? 999 : 999999; // 999
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
		System.out.println("Running Simulated Annealing (" + maxIteration + " Loops):\n");
		System.out.println("Starting energy: " + currEnergy);
		for (int i = 0; i < maxIteration && temperature > 0.1; i++) {
			if (Common.SHOW_SA_DEBUG)
				System.out.println("\nLoop " + i + " (temperature: " + temperature + ", bestEnergy: " + bestEnergy + "):");
			// random move to students to get new students.
			// depending on temperature,
			// if higher, make more risky random moves.
			// else, make more conservative moves.
			nextStudents = makeRandomMove(currStudents);

			// compute new energy.
			nextEnergy = calculateEnergy(nextStudents);

			// decide if accept this new solution.
			double randomProbability = new Random().nextDouble();
			double energyDifference = nextEnergy - currEnergy;
			double acceptanceProbability = calculateAcceptanceProbability(currEnergy, nextEnergy, temperature);
			if (Common.SHOW_SA_DEBUG)
				System.out.println("currEnergy: " + currEnergy);
			if (Common.SHOW_SA_DEBUG)
				System.out.println("nextEnergy: " + nextEnergy);
			if (Common.SHOW_SA_DEBUG)
				System.out.println("energyDifference: " + energyDifference);
			if (Common.SHOW_SA_DEBUG)
				System.out.println("energyDifference / temperature: " + energyDifference / temperature);
			if (Common.SHOW_SA_DEBUG)
				System.out.println("Acceptance Probability: " + acceptanceProbability);
			if (Common.SHOW_SA_DEBUG)
				System.out.println("Random Probability: " + randomProbability);
			if (Common.SHOW_SA_DEBUG)
				System.out
						.println("randomProbability <= acceptanceProbability: " + (randomProbability <= acceptanceProbability));
			if (randomProbability <= acceptanceProbability) {
				currStudents = nextStudents;
				currEnergy = nextEnergy;
				if (Common.SHOW_SA_DEBUG)
					System.out.println("New candidate solution accepted.");
			}

			// keep track of the best solution found, i.e. next lowest energy.
			if (nextEnergy < bestEnergy) {
				bestStudents = nextStudents;
				bestEnergy = nextEnergy;
				System.out.println("\nNew best solution found.");
				System.out.println("Satisfaction: " + storedSatisfaction);
				System.out.println("1.0 / Satisfaction * 100000: " + calculateEnergy(nextStudents));
				System.out.println("currEnergy: " + currEnergy);
				System.out.println("nextEnergy: " + nextEnergy + "\n");
			}

			// cool system.
			temperature *= 1 - coolingRate;
		}
		System.out.println("bestEnergy: " + bestEnergy);
		System.out.println("currEnergy: " + currEnergy);
		System.out.println(toStringStudents(bestStudents));
		return bestStudents;
	}

	private String toStringStudents(ArrayList<Student> students) {
		String res = "";
		for (Student student : students) {
			res += student + "\n";
		}
		return res;
	}

	private Double calculateAcceptanceProbability(double currEnergy, double nextEnergy, double temperature) {
		if (nextEnergy < currEnergy)
			return 1.0;
		else
			return Math.exp((currEnergy - nextEnergy) / temperature);
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

		// if (newStudents.get(s1).getStream().equals(newStudents.get(s2).getStream())) {
		// 	System.out.println("Students have incompatible stream.");
		// }

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
		return 1.0 / calculateGlobalSatisfaction(students) * 100000;
	}

	private Double calculateGlobalSatisfaction(ArrayList<Student> students) {
		Double satisfaction = 0.0;
		for (Student student : students) {
			satisfaction += student.calculateSatisfaction();
		}

		// if less than or equal to 1, complement of satisfaction (i.e. 1 / satisfaction) would be an opposite effect, so we limit satisfaction minimum limit to 2.0.
		if (satisfaction <= 1) {
			satisfaction = 2.0;
		}

		storedSatisfaction = satisfaction;
		return satisfaction;
	}

	// method check if projects equally distributed across supervisors.
	// if no, has cost.

	// method check if higher GPA means a greater chance of getting one's preferred projects.
	// add in student class?
}
