package ie.ucd.solvers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import ie.ucd.objects.CandidateSolution;
import ie.ucd.Common;
import ie.ucd.objects.Project;
import ie.ucd.objects.BitCodeSolution;
import ie.ucd.objects.Student;
import ie.ucd.ui.common.sheets.Sheets;
import ie.ucd.ui.common.sheets.StudentSheet;
import ie.ucd.ui.interfaces.VisualizerInterface;
import ie.ucd.ui.solver.SolverPane;

public class GeneticAlgorithm extends Solver implements SolverUIUpdater {
	private double mutationChance;
	private double crossoverChance;
	private double cullPercentage;
	private final double cullPercentageIncrementFactor;
	private int numberOfGenerations;
	private int sizeOfPopulation;

	private CandidateSolution startingSolution;
	private CandidateSolution finalSolution;
	private double finalSolutionFitness;

	private final Random random = new Random();
	private SolverPane solverPane;

	public GeneticAlgorithm(CandidateSolution startingSolution) {
		this(startingSolution, null);
	}

	public GeneticAlgorithm(CandidateSolution startingSolution, SolverPane solverPane) {
		this(0.05, 0.4, 125, 80, 0.25, startingSolution, solverPane);
	}

	public GeneticAlgorithm(double mutationChance, double crossoverChance, int numberOfGenerations, int sizeOfPopulation,
			double cullPercentage, CandidateSolution startingSolution, SolverPane solverPane) {
		this.mutationChance = mutationChance;
		this.crossoverChance = crossoverChance;
		this.numberOfGenerations = numberOfGenerations;
		this.sizeOfPopulation = sizeOfPopulation;
		this.cullPercentage = cullPercentage;
		this.cullPercentageIncrementFactor = (double) Math
				.round(((1 - cullPercentage) / numberOfGenerations) * 1000d) / 1000d;
		this.startingSolution = startingSolution;
		this.solverPane = solverPane;
		if (Common.DEBUG_SHOW_GA) {
			System.out.println("Calculated culling increment factor = " + cullPercentageIncrementFactor);
		}
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
		CandidateSolution nextPossibleSolution = currSolution;
		CandidateSolution fittestSolution = currSolution;
		CandidateSolution bestSolution = currSolution;
		uiAddToCurrQueueAnimate(currSheet, currSolution);
		uiAddToBestQueueAnimate(bestSheet, bestSolution);

		// keep track of satisfaction/fitness.
		Double bestSatisfaction = currSolution.calculateGlobalSatisfaction();

		// generate bit codes to represent chromosomes.
		ArrayList<String> allBitCodes = generateAllBitCodes(currSolution.getStudents().size());

		// generate population for generation 0.
		BitCodeSolution[] currPopulation = generateInitialPopulation(allBitCodes);
		BitCodeSolution[] nextPopulation = new BitCodeSolution[sizeOfPopulation];

		System.out.println("Running Genetic Algorithm (" + numberOfGenerations + " Generations):\n");
		System.out.println("Starting satisfaction: " + bestSatisfaction);
//		ArrayList<Double> populationSatisfactions = new ArrayList<Double>();
		Integer fittestIndex = 0;
		Double fittestSatisfaction = Double.NEGATIVE_INFINITY;
		String fittestBitCodeSolution = "";
		uiSignalNewGraph(visualizer);
		for (int i = 1; i <= numberOfGenerations && threadStillRunning(); i++) {
			if (Common.DEBUG_SHOW_GA)
				System.out.println("Creating Generation #" + i);

			// calculate and store satisfaction for each bitCodeSolution in a population.
			// at the same time, find fittest solution.
//			populationSatisfactions = new ArrayList<Double>();
			for (int j = 0; j < currPopulation.length; j++) {
				String bitCodeSolution = currPopulation[j].getSolution();
				nextPossibleSolution = assignProjectsFromBitCodeSolution(bitCodeSolution, startingSolution);
				// ArrayList<Project> updatedProjects = updateProjects(projects, students.size());
				double populationSatisfaction = nextPossibleSolution.calculateGlobalSatisfaction();
				if (Common.DEBUG_SHOW_GA)
					System.out.println("populationSatisfaction: " + populationSatisfaction);
//				populationSatisfactions.add(populationSatisfaction);
				currPopulation[j].setSatisfaction(populationSatisfaction);

				// find fittest solution in population.
				if (populationSatisfaction > fittestSatisfaction) {
					fittestIndex = j;
					fittestSatisfaction = populationSatisfaction;
					fittestBitCodeSolution = bitCodeSolution;
					fittestSolution = nextPossibleSolution;
				}
				uiAddToGraph(visualizer, populationSatisfaction, bestSatisfaction, i);
			}
			if (Common.DEBUG_SHOW_GA) {
				System.out.println("Fittest solution strength: " + fittestSatisfaction);
			}
			uiAddToCurrQueueAnimate(currSheet, fittestSolution);
			// keep track of the best solution found, i.e. next highest fitness/satisfaction.
			if (fittestSatisfaction > bestSatisfaction) {
				bestSolution = fittestSolution;
				bestSatisfaction = fittestSatisfaction;
				uiAddToBestQueueAnimate(bestSheet, bestSolution);
			}

			// generate population for next generation.
			int counter = 0;
			while (counter < sizeOfPopulation) {
				BitCodeSolution[] populationAfterSorting = descendingBubbleSort(currPopulation);
				BitCodeSolution[] populationAfterCulling = cull(populationAfterSorting);
				String[] parents = getParents(populationAfterCulling);
				if (parents[0] == null || parents[1] == null)
					System.out.println(parents[0] + "\n" + parents[1]);
				String offspring = crossover(parents[0], parents[1]);
				if (! offspring.equals("")) {
					offspring = mutate(offspring);
					nextPopulation[counter] = new BitCodeSolution(offspring);
					counter++;
				}
			}
			currPopulation = nextPopulation;
			nextPopulation = new BitCodeSolution[sizeOfPopulation];
			incrementCullPercentage();

			threadHandleOneStepAndWaiting();
			uiAddToProgressIndicator(solverPane, 1.0, i * 1.0, numberOfGenerations * 1.0);
		}
		// get best final solution from final generation.
		finalSolutionFitness = currPopulation[fittestIndex].getSatisfaction();
		this.finalSolution = assignProjectsFromBitCodeSolution(fittestBitCodeSolution, fittestSolution);

		uiAddToCurrQueueNoAnimate(currSheet, fittestSolution);
		uiAddToBestQueueNoAnimate(bestSheet, bestSolution);
		uiSignalProcessingDone(solverPane);

		System.out.println("Genetic Algorithm simulation complete.");
	}

	private String[] getParents(BitCodeSolution[] bitCodeSolutions) {
		String[] parents = new String[2];
		double max = 1.0, secondMax = 1.0; // arbitrary positive values.
		int randomIndex1 = random.nextInt(bitCodeSolutions.length);
		int randomIndex2 = random.nextInt(bitCodeSolutions.length);
		while(randomIndex1 == randomIndex2)
			randomIndex2 = random.nextInt(bitCodeSolutions.length);

		parents[0] = bitCodeSolutions[randomIndex1].getSolution();
		parents[1] = bitCodeSolutions[randomIndex2].getSolution();

		return parents;
	}

	private BitCodeSolution[] descendingBubbleSort(BitCodeSolution[] population) {
		int n = population.length;
		for (int i = 0; i < n-1; i++) {
			for (int j = 0; j < n - i - 1; j++) {
				if (population[j].getSatisfaction() < population[j + 1].getSatisfaction()) {
					BitCodeSolution temp = population[j];
					population[j] = population[j + 1];
					population[j + 1] = temp;
				}
			}
		}
		return population;
	}

	private String crossover(String parentA, String parentB) {
		String offspring = "";
		// use crossover probability.
		int bound = (int) (crossoverChance * 1000);
		int probabilityIndex = random.nextInt(1000);

		if (probabilityIndex < bound) {
			int crossoverIndex = random.nextInt((parentA.length() - 1) * 10) / 10;
			offspring = parentA.substring(0, crossoverIndex).concat(parentB.substring(crossoverIndex));
		}
		// if crossover didn't occur offspring will be a blank String.
		return offspring;
	}

	private String mutate(String bitCodeSolution) {
		char[] solutionArray = bitCodeSolution.toCharArray();
		// use mutation probability.
		int bound = (int) (mutationChance * 1000);
		int probabilityIndex = random.nextInt(1000);

		if (probabilityIndex < bound) {
			int randomIndex = random.nextInt(bitCodeSolution.length() * 10) / 10;
			if (solutionArray[randomIndex] == '0')
				solutionArray[randomIndex] = '1';
			else
				solutionArray[randomIndex] = '0';
		}

		return String.valueOf(solutionArray);
	}

	private BitCodeSolution[] cull(BitCodeSolution[] sortedPopulation) {
		int sizeAfterCulling = (int) (sortedPopulation.length * (1 - cullPercentage));
		BitCodeSolution[] culledPopulation = new BitCodeSolution[sizeAfterCulling];
		System.arraycopy(sortedPopulation, 0, culledPopulation, 0, sizeAfterCulling);
		return culledPopulation;
	}

	private ArrayList<String> generateAllBitCodes(int numberOfBitCodes) {
		String currentBitCode = get1stBitCode();
		ArrayList<String> allBitCodes = new ArrayList<String>();
		allBitCodes.add(currentBitCode);
		// generate remaining bit codes.
		for (int i = 1; i < numberOfBitCodes; i++) {
			String nextBitCode = getNextBitCode(currentBitCode);
			// ensure new bit code is not a duplicate of an older one.
			while (allBitCodes.contains(nextBitCode))
				nextBitCode = getNextBitCode(currentBitCode);
			allBitCodes.add(nextBitCode);
			currentBitCode = nextBitCode;
		}
		return allBitCodes;
	}

	private String get1stBitCode() {
		return "0000000000";
	}

	private String getNextBitCode(String previousBitCode) {
		// pick random bit 0-9 to flip.
		int randomIndex = random.nextInt(100) / 10;

		// char array makes replacing a bit more elegant and convenient.
		char[] nextBitCode = previousBitCode.toCharArray();
		char[] flipTo = new char[1];

		// check what the randomly chosen bit is and flip it.
		if (Character.compare(nextBitCode[randomIndex], '0') == 0) {
			flipTo[0] = '1';
			nextBitCode[randomIndex] = flipTo[0];
		} else if (Character.compare(nextBitCode[randomIndex], '1') == 0) {
			flipTo[0] = '0';
			nextBitCode[randomIndex] = flipTo[0];
		}
		// parse char array to String and return.
		return String.valueOf(nextBitCode);
	}

	private int toDecimal(String bitCode) {
		return Integer.parseInt(bitCode, 2);
	}

	private BitCodeSolution[] generateInitialPopulation(ArrayList<String> allBitCodes) {
		// create the first solution which usually has very good strength.
		int[] orderOfBitCodes = get1stOrderOfBitCodes(allBitCodes.size());
		String bitCodeSolution = generateBitCodeSolution(allBitCodes, orderOfBitCodes);
		bitCodeSolution = mutate(bitCodeSolution);

		// add first solution to population.
		BitCodeSolution[] population = new BitCodeSolution[sizeOfPopulation];
		population[0] = new BitCodeSolution(bitCodeSolution);

		HashSet<String> usedSolutions = new HashSet<String>();

		// each following solution builds on the previous one by swapping around.
		// any two random positions in orderOfBitCodes.
		for (int i = 1; i < sizeOfPopulation; i++) {
			int[] previousOrder = orderOfBitCodes;
			orderOfBitCodes = getNextOrderOfBitCodes(previousOrder);
			bitCodeSolution = generateBitCodeSolution(allBitCodes, orderOfBitCodes);
			bitCodeSolution = mutate(bitCodeSolution);
			while (usedSolutions.contains(bitCodeSolution)) {
				orderOfBitCodes = getNextOrderOfBitCodes(orderOfBitCodes);
				bitCodeSolution = generateBitCodeSolution(allBitCodes, orderOfBitCodes);
				bitCodeSolution = mutate(bitCodeSolution);
			}
			population[i] = new BitCodeSolution(bitCodeSolution);
			usedSolutions.add(bitCodeSolution);
		}

		return population;
	}

	private String generateBitCodeSolution(ArrayList<String> allBitCodes, int[] orderOfBitCodes) {
		String candidateSolution = "";
		for (int index : orderOfBitCodes) {
			candidateSolution = candidateSolution.concat(allBitCodes.get(index));
		}
		return candidateSolution;
	}

	private int[] get1stOrderOfBitCodes(int numberOfBitCodes) {
		int randomIndex;
		if (Common.DEBUG_SHOW_GA) {
			System.out.println("Generating first order");
		}
		int[] orderOfBitCodes = new int[numberOfBitCodes];
		HashSet<Integer> usedNumbers = new HashSet<Integer>();
		// generate a random arrangement pattern for bit codes in a solution
		for (int i = 0; i < numberOfBitCodes; i++) {
			randomIndex = random.nextInt(numberOfBitCodes * 10) / 10;
			while (usedNumbers.contains(randomIndex))
				randomIndex = random.nextInt(numberOfBitCodes * 10) / 10;
			orderOfBitCodes[i] = randomIndex;
			usedNumbers.add(randomIndex);
			if (Common.DEBUG_SHOW_GA) {
				System.out.println(randomIndex);
			}
		}
		return orderOfBitCodes;
	}

	private int[] getNextOrderOfBitCodes(int[] previousOrderOfBitCodes) {
		int randomIndex = random.nextInt(previousOrderOfBitCodes.length - 1);
		// swap number at randomIndex with the next one
		int temp = previousOrderOfBitCodes[randomIndex];
		previousOrderOfBitCodes[randomIndex] = previousOrderOfBitCodes[randomIndex + 1];
		previousOrderOfBitCodes[randomIndex + 1] = temp;
		return previousOrderOfBitCodes;
	}

	// assign projects to students, based on the solution.
	private CandidateSolution assignProjectsFromBitCodeSolution(String bitCodeSolution, CandidateSolution currSolution) {
		CandidateSolution newSolution = new CandidateSolution(currSolution);
		ArrayList<Student> students = newSolution.getStudents();
		ArrayList<Project> projects = newSolution.getProjects();

		int[] studentRankings = getRanking(bitCodeSolution, students.size());
		ArrayList<Student> assignedStudents = new ArrayList<Student>();
		ArrayList<Student> unassignedStudents = new ArrayList<Student>();
		HashSet<Project> usedProjects = new HashSet<Project>();

		// assign projects to students.
		for (int rank = 1; rank <= students.size(); rank++) {
			for (int j = 0; j < studentRankings.length; j++) {
				if (rank == studentRankings[j]) {
					Student currentStudent = students.get(j);
					ArrayList<Project> currentPrefList = currentStudent.getPreferenceList();
					boolean isAssigned = false;
					for (Project project : currentPrefList) {
						if (usedProjects.contains(project))
							continue;
						currentStudent.setProjectAssigned(project, 0);
						usedProjects.add(project);
						isAssigned = true;
						break;
					}
					if (isAssigned)
						assignedStudents.add(currentStudent);
					else
						unassignedStudents.add(currentStudent);
				}
			}
		}

		// assign projects to remaining unassigned students.
		for (Student student : unassignedStudents) {
			Project randomProject = projects.get(random.nextInt(projects.size()));
			while (usedProjects.contains(randomProject)) {
				randomProject = projects.get(random.nextInt(projects.size()));
			}
			student.setProjectAssigned(randomProject, 0);
			assignedStudents.add(student);
		}

		newSolution.calculateProjectSatisfactionAndUpdateProjectViolation();
		return newSolution;
	}

	private int[] getRanking(String bitCodeSolution, int numberOfBitCodes) {
		int low = 0, high = 10, offset = high - low;
		int[] solutionInDecimal = new int[numberOfBitCodes];

		for (int i = 0; i < numberOfBitCodes; i++) {
			solutionInDecimal[i] = toDecimal(bitCodeSolution.substring(low, high));
			low += offset;
			high += offset;
		}

		int[] ranking = new int[numberOfBitCodes];
		for (int i = 0; i < solutionInDecimal.length; i++) {
			int count = 0;
			for (int value : solutionInDecimal) {
				if (value > solutionInDecimal[i]) {
					count++;
				}
			}
			ranking[i] = count + 1;
		}
		return ranking;
	}

	private void incrementCullPercentage() {
		cullPercentage += cullPercentageIncrementFactor;
		if (cullPercentage > 0.97)
			cullPercentage = 0.97; // limit probability
	}

	public CandidateSolution getBestSolution() {
		return finalSolution;
	}

	public double getFinalSolutionFitness() {
		return finalSolutionFitness;
	}
}
