package ie.ucd.solvers;

import java.util.Random;
import ie.ucd.Settings;
import ie.ucd.objects.CandidateSolution;
import ie.ucd.objects.Project;
import ie.ucd.objects.Student;
import ie.ucd.ui.common.sheets.Sheets;
import ie.ucd.ui.common.sheets.StudentSheet;
import ie.ucd.ui.interfaces.VisualizerInterface;
import ie.ucd.ui.solver.SolverPane;

public class SimulatedAnnealing extends Solver implements SolverUIUpdater {
	private double storedSatisfaction;
	private CandidateSolution bestSolution;

	private CandidateSolution startingSolution;
	private double currTemperature;
	private double startTemperature;
	private double minTemperature;
	private double coolingRate;
	private double maxIteration;

	private SolverPane solverPane;

	public SimulatedAnnealing(CandidateSolution startingSolution) {
		this(startingSolution, null);
	}

	public SimulatedAnnealing(CandidateSolution startingSolution, SolverPane solverPane) {
		// this(100.0, 0.0001, 0.000000000000001, 10000000, startingSolution, solverPane);
		this(100.0, 0.001, 0.001, 50000, startingSolution, solverPane);
	}

	public SimulatedAnnealing(double startTemperature, double coolingRate, double minTemperature, double maxIteration,
			CandidateSolution startingSolution, SolverPane solverPane) {
		this.currTemperature = startTemperature;
		this.coolingRate = coolingRate;
		this.startTemperature = startTemperature;
		this.minTemperature = minTemperature;
		this.maxIteration = maxIteration;
		this.startingSolution = startingSolution;
		this.solverPane = solverPane;
	}

	public CandidateSolution getBestSolution() {
		return bestSolution;
	}

	public void run() {
		VisualizerInterface visualizer = solverPane.getVisualizer();
		Sheets sheets = solverPane.getSheets();

		StudentSheet currSheet = null;
		StudentSheet bestSheet = null;
		if (sheets != null) {
			currSheet = sheets.getCurrentSheet();
			bestSheet = sheets.getBestSheet();
		}

		// keep track of solutions.
		CandidateSolution currSolution = startingSolution;
		CandidateSolution nextSolution;
		CandidateSolution bestSolution = currSolution;
		uiAddToCurrQueueAnimate(currSheet, currSolution);
		uiAddToBestQueueAnimate(bestSheet, bestSolution);

		// keep track of energy.
		double currEnergy = calculateEnergy(currSolution);
		double nextEnergy;
		double bestEnergy = currEnergy;

		System.out.println("Running Simulated Annealing (" + maxIteration + " Loops):\n");
		System.out.println("Starting energy: " + currEnergy);
		int totalRejected = 0;
		int totalStraightAccept = 0;
		int i = 0;
		uiSignalNewGraph(visualizer);
		for (i = 0; i < maxIteration && currTemperature > minTemperature && threadStillRunning(); i++) {
			// random move to students to get new students.
			// depending on currTemperature,
			// if higher, make more risky random moves.
			// else, make more conservative moves.
			switch (Settings.SA_RANDOM_MOVE_TYPE) {
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
			double acceptanceProbability = calculateAcceptanceProbability(currEnergy, nextEnergy, currTemperature);
			if (randomProbability <= acceptanceProbability) {
				currSolution = nextSolution;
				currEnergy = nextEnergy;
				uiAddToCurrQueueAnimate(currSheet, currSolution);
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
				uiAddToBestQueueAnimate(bestSheet, bestSolution);
			}

			// cool system. (not much difference between the below two configurations)
			currTemperature *= 1 - coolingRate; // exponential decrease.
			// currTemperature = startTemperature * ((maxIteration - i + 1.0) / maxIteration); // linear decrease;

			uiAddToGraph(visualizer, currEnergy, bestEnergy, i);
			threadHandleOneStepAndWaiting();
			uiAddToProgressIndicator(solverPane, startTemperature, currTemperature, minTemperature);
		}
		uiAddToCurrQueueNoAnimate(currSheet, currSolution);
		uiAddToBestQueueNoAnimate(bestSheet, bestSolution);
		uiSignalProcessingDone(solverPane);

		System.out.println("\nExited at loop " + i + ", currTemperature " + currTemperature);
		System.out.println("totalRejected: " + totalRejected);
		System.out.println("totalStraightAccepted (i.e. prob = 1.0): " + totalStraightAccept);
		System.out.println("currEnergy: " + currEnergy);
		System.out.println("currSatisfaction: " + currSolution.calculateGlobalSatisfaction());
		System.out.println("bestEnergy: " + bestEnergy);
		System.out.println("bestSatisfaction: " + bestSolution.calculateGlobalSatisfaction());
		this.bestSolution = bestSolution;
	}

	private Double calculateAcceptanceProbability(double currEnergy, double nextEnergy, double currTemperature) {
		if (nextEnergy < currEnergy)
			return 1.0;
		else
			return Math.exp((currEnergy - nextEnergy) / currTemperature);
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

		newSolution.calculateProjectSatisfactionAndUpdateProjectViolation();
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

		newSolution.calculateProjectSatisfactionAndUpdateProjectViolation();
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

		newSolution.calculateProjectSatisfactionAndUpdateProjectViolation();
		return newSolution;
	}

	public Double calculateEnergy(CandidateSolution solution) {
		storedSatisfaction = solution.calculateGlobalSatisfaction();
		return (1.0 / storedSatisfaction) * 100000; // scale up.
	}
}
