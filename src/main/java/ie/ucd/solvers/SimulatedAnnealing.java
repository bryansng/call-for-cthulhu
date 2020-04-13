package ie.ucd.solvers;

import java.util.Random;
import ie.ucd.Common;
import ie.ucd.objects.CandidateSolution;
import ie.ucd.objects.Project;
import ie.ucd.objects.Student;
import ie.ucd.ui.common.sheets.Sheets;
import ie.ucd.ui.common.sheets.StudentSheet;
import ie.ucd.ui.interfaces.VisualizerInterface;

public class SimulatedAnnealing extends Solver {
	private double storedSatisfaction;
	private CandidateSolution bestSolution;

	private CandidateSolution startingSolution;
	private double temperature;
	private double startTemperature;
	private double minTemperature;
	private double coolingRate;
	private double maxIteration;
	private VisualizerInterface visualizer;
	private StudentSheet currSheet;
	private StudentSheet bestSheet;

	public SimulatedAnnealing(CandidateSolution startingSolution) {
		this(startingSolution, null, null);
	}

	public SimulatedAnnealing(CandidateSolution startingSolution, VisualizerInterface visualizer, Sheets sheets) {
		this(100.0, 0.001, 0.01, 10000, startingSolution, visualizer, sheets);
	}

	public SimulatedAnnealing(double temperature, double coolingRate, double minTemperature, double maxIteration,
			CandidateSolution startingSolution, VisualizerInterface visualizer, Sheets sheets) {
		this.temperature = temperature;
		this.coolingRate = coolingRate;
		this.startTemperature = temperature;
		this.minTemperature = minTemperature;
		this.maxIteration = maxIteration;
		this.startingSolution = startingSolution;
		this.visualizer = visualizer;
		if (sheets != null) {
			this.currSheet = sheets.getCurrentSheet();
			this.bestSheet = sheets.getBestSheet();
		}
	}

	public CandidateSolution getBestSolution() {
		return bestSolution;
	}

	public void run() {
		// keep track of solutions.
		CandidateSolution currSolution = startingSolution;
		CandidateSolution nextSolution;
		CandidateSolution bestSolution = currSolution;
		if (currSheet != null) {
			currSheet.setAll(currSolution.getStudents());
			currSheet.updateStrengthAndConstraints(currSolution);
		}
		if (bestSheet != null) {
			bestSheet.setAll(bestSolution.getStudents());
			bestSheet.updateStrengthAndConstraints(currSolution);
		}

		// minimizing negative fitness == maximizing positive fitness?
		double currEnergy = calculateEnergy(currSolution);
		double nextEnergy;
		double bestEnergy = currEnergy;

		System.out.println("Running Simulated Annealing (" + maxIteration + " Loops):\n");
		System.out.println("Starting energy: " + currEnergy);
		int totalRejected = 0;
		int totalStraightAccept = 0;
		int i = 0;
		if (visualizer != null)
			visualizer.newSeries();
		for (i = 0; i < maxIteration && temperature > minTemperature && !this.isStopped; i++) {
			try {
				// random move to students to get new students.
				// depending on temperature,
				// if higher, make more risky random moves.
				// else, make more conservative moves.
				switch (Common.SA_RANDOM_MOVE_TYPE) {
					case FROM_STUDENT_PREFERENCE_LIST:
						nextSolution = makeRandomMoveV3(currSolution);
						break;
					case SWAP_FROM_TWO_STUDENTS_ASSIGNED_PROJECTS:
						nextSolution = makeRandomMoveV1(currSolution);
						break;
					case SWAP_FROM_TWO_STUDENTS_PREFERENCE_LIST:
					default:
						nextSolution = makeRandomMoveV2(currSolution);
						break;
				}

				// compute new energy.
				nextEnergy = calculateEnergy(nextSolution);

				// decide if accept this new solution.
				double randomProbability = new Random().nextDouble();
				double acceptanceProbability = calculateAcceptanceProbability(currEnergy, nextEnergy, temperature);
				if (randomProbability <= acceptanceProbability) {
					currSolution = nextSolution;
					currEnergy = nextEnergy;
					if (currSheet != null) {
						currSheet.setAll(currSolution.getStudents());
						currSheet.updateStrengthAndConstraints(currSolution);
					}
				} else {
					totalRejected += 1;
				}
				if (acceptanceProbability == 1.0) {
					totalStraightAccept += 1;
				}

				// keep track of the best solution found, i.e. next lowest energy.
				if (nextEnergy < bestEnergy) {
					bestSolution = nextSolution;
					bestEnergy = nextEnergy;
					if (bestSheet != null) {
						bestSheet.setAll(bestSolution.getStudents());
						bestSheet.updateStrengthAndConstraints(currSolution);
					}
				}

				// cool system. (not much difference between the below two configurations)
				temperature *= 1 - coolingRate; // exponential decrease.
				// temperature = startTemperature * ((maxIteration - i + 1.0) / maxIteration); // linear decrease;

				if (visualizer != null)
					visualizer.addToQueue(currEnergy, bestEnergy, i);

				if (this.isOneStep) {
					this.oneStepDone();
				}
				if (this.isSuspended) {
					synchronized (this) {
						while (this.isSuspended) {
							wait();
						}
					}
				}
			} catch (InterruptedException e) {
			}
		}
		System.out.println("\nExited at loop " + i + ", temperature " + temperature);
		System.out.println("totalRejected: " + totalRejected);
		System.out.println("totalStraightAccepted (i.e. prob = 1.0): " + totalStraightAccept);
		System.out.println("currEnergy: " + currEnergy);
		System.out.println("currSatisfaction: " + currSolution.calculateGlobalSatisfaction());
		System.out.println("bestEnergy: " + bestEnergy);
		System.out.println("bestSatisfaction: " + bestSolution.calculateGlobalSatisfaction());
		this.bestSolution = bestSolution;
	}

	private Double calculateAcceptanceProbability(double currEnergy, double nextEnergy, double temperature) {
		if (nextEnergy < currEnergy)
			return 1.0;
		else
			return Math.exp((currEnergy - nextEnergy) / temperature);
	}

	// pick another project in a student's preference list.
	public CandidateSolution makeRandomMoveV3(CandidateSolution currSolution) {
		CandidateSolution newSolution = new CandidateSolution(currSolution);

		// get a random student.
		Random rand = new Random();
		int index = rand.nextInt(newSolution.getStudents().size());
		Student student = newSolution.getStudents().get(index);

		// replace student's assigned project with another in preference list.
		student.setProjectAssigned(student.getPreferenceList().get(rand.nextInt(student.getPreferenceList().size())), 0);

		return newSolution;
	}

	// between two students, between their preference list, a student is assigned a project in that other student's list, and the other student with the same concept as well
	public CandidateSolution makeRandomMoveV2(CandidateSolution currSolution) {
		CandidateSolution newSolution = new CandidateSolution(currSolution);

		// get random two students.
		Random rand = new Random();
		int s1Index = rand.nextInt(newSolution.getStudents().size());
		int s2Index = rand.nextInt(newSolution.getStudents().size());
		while (s1Index == s2Index) {
			s2Index = rand.nextInt(newSolution.getStudents().size());
		}

		Student student1 = newSolution.getStudents().get(s1Index);
		Student student2 = newSolution.getStudents().get(s2Index);

		int p1Index = rand.nextInt(student1.getPreferenceList().size());
		int p2Index = rand.nextInt(student2.getPreferenceList().size());
		student1.setProjectAssigned(student2.getPreferenceList().get(p2Index), 0);
		student2.setProjectAssigned(student1.getPreferenceList().get(p1Index), 0);

		return newSolution;
	}

	// change project assigned between two students.
	public CandidateSolution makeRandomMoveV1(CandidateSolution currSolution) {
		CandidateSolution newSolution = new CandidateSolution(currSolution);

		// get random two students.
		Random rand = new Random();
		int s1Index = rand.nextInt(newSolution.getStudents().size());
		int s2Index = rand.nextInt(newSolution.getStudents().size());
		while (s1Index == s2Index) {
			s2Index = rand.nextInt(newSolution.getStudents().size());
		}

		Student student1 = newSolution.getStudents().get(s1Index);
		Student student2 = newSolution.getStudents().get(s2Index);

		// swap student assigned projects.
		Project p1 = student1.getProjectAssigned(0);
		student1.setProjectAssigned(student2.getProjectAssigned(0), 0);
		student2.setProjectAssigned(p1, 0);

		return newSolution;
	}

	public Double calculateEnergy(CandidateSolution solution) {
		storedSatisfaction = solution.calculateGlobalSatisfaction();
		return (1.0 / storedSatisfaction) * 100000;
	}
}
