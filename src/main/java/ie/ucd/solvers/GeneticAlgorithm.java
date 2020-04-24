package ie.ucd.solvers;

import java.util.ArrayList;
import java.util.Arrays;
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

	double plateauPercentage;
	boolean isPlateauReached;
	int plateauCheckFrom;
	int minRepetitionsForPlateau;


	private final Random random = new Random();
	private SolverPane solverPane;

	public GeneticAlgorithm(CandidateSolution startingSolution) {
		this(startingSolution, null);
	}

	public GeneticAlgorithm(CandidateSolution startingSolution, SolverPane solverPane) {
		this(0.2, 0.7, 200, 1000, 0.25, startingSolution, solverPane);
	}

	public GeneticAlgorithm(double mutationChance, double crossoverChance, int numberOfGenerations, int sizeOfPopulation,
			double cullPercentage, CandidateSolution startingSolution, SolverPane solverPane) {
		this.mutationChance = mutationChance;
		this.crossoverChance = crossoverChance;
		this.numberOfGenerations = numberOfGenerations;
		this.sizeOfPopulation = sizeOfPopulation;
		this.cullPercentage = cullPercentage;
		this.cullPercentageIncrementFactor = (double) Math.round(((1 - cullPercentage) / numberOfGenerations) * 1000d)
				/ 1000d;
		this.startingSolution = startingSolution;
		this.solverPane = solverPane;
		this.plateauPercentage = 0.45;
		this.plateauCheckFrom = (int) ((1.0 - plateauPercentage) * numberOfGenerations);
		this.minRepetitionsForPlateau = (int) (0.2 * plateauPercentage * numberOfGenerations) - 1;
		this.isPlateauReached = false;
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

		//keep track of plateau
		ArrayList<CandidateSolution> possiblePlateauSolutions = new ArrayList<CandidateSolution>();

		// keep track of satisfaction/fitness.
		Double fittestSatisfaction = Double.NEGATIVE_INFINITY;
		Double bestSatisfaction = currSolution.calculateGlobalSatisfaction();

		// generate bit codes to represent chromosomes.
		ArrayList<String> allBitCodes = generateAllBitCodes(currSolution.getStudents().size());

		// generate population for generation 0.
		BitCodeSolution[] currPopulation = generateInitialPopulation(allBitCodes);
		BitCodeSolution[] nextPopulation = new BitCodeSolution[sizeOfPopulation];

		System.out.println("Running Genetic Algorithm (" + numberOfGenerations + " Generations):\n");
		System.out.println("Starting satisfaction: " + bestSatisfaction);

		uiSignalNewGraph(visualizer);

		//begin generation loop
		for (int i = 1; i <= numberOfGenerations && threadStillRunning(); i++) {
			if (Common.DEBUG_SHOW_GA)
				System.out.println("Creating Generation #" + i);

			// calculate and store satisfaction for each bitCodeSolution in a population.
			// at the same time, find fittest solution.
			fittestSatisfaction = Double.NEGATIVE_INFINITY;
			for (BitCodeSolution member : currPopulation) {
				String bitCodeSolution = member.getSolution();
				nextPossibleSolution = assignProjectsFromBitCodeSolution(bitCodeSolution, startingSolution);
				double solutionSatisfaction = nextPossibleSolution.calculateGlobalSatisfaction();
				if (Common.DEBUG_SHOW_GA)
					System.out.println("solutionSatisfaction: " + solutionSatisfaction);
				member.setSatisfaction(solutionSatisfaction);

				// find fittest solution in population.
				if (solutionSatisfaction > fittestSatisfaction) {
					if (Common.DEBUG_SHOW_GA)
						System.out.println(String.format("Changed fittest Satisfaction: was %f, is now %f", fittestSatisfaction,
								solutionSatisfaction));
					fittestSatisfaction = solutionSatisfaction;
					fittestSolution = nextPossibleSolution;
				}
				uiAddToGraph(visualizer, solutionSatisfaction, bestSatisfaction, i);
			}
			uiAddToCurrQueueAnimate(currSheet, fittestSolution);


			// sort and cull population.
			sort(currPopulation);
			System.out.println("worst: " + currPopulation[sizeOfPopulation - 1].getSatisfaction());
			BitCodeSolution[] populationAfterCulling = cull(currPopulation);
			if (Common.DEBUG_SHOW_GA) {
				System.out.println("Fittest solution strength: " + fittestSatisfaction);
			}

			// keep track of the best solution found, i.e. next highest fitness/satisfaction.
			if (fittestSatisfaction > bestSatisfaction) {
				bestSolution = fittestSolution;
				bestSatisfaction = fittestSatisfaction;
				uiAddToBestQueueAnimate(bestSheet, bestSolution);
			}

			// look for plateau.
			if (i >= plateauCheckFrom) {
				checkForPlateau(possiblePlateauSolutions, bestSatisfaction);
				if (isPlateauReached) {
					break;
				} else {
					possiblePlateauSolutions.add(bestSolution);
				}
			}

			//generate the next population
			currPopulation = generateNextPopulation(populationAfterCulling);

			//prepare for next iteration
			incrementCullPercentage();

			threadHandleOneStepAndWaiting();
			uiAddToProgressIndicator(solverPane, 1.0, i * 1.0, numberOfGenerations * 1.0);
			System.out.println(i + "  " + fittestSatisfaction + "  " + bestSatisfaction);
		}

		// get best final solution
		finalSolutionFitness = bestSatisfaction;
		this.finalSolution = bestSolution;

		uiAddToCurrQueueNoAnimate(currSheet, fittestSolution);
		uiAddToBestQueueNoAnimate(bestSheet, bestSolution);
		uiSignalProcessingDone(solverPane);

		System.out.println("Genetic Algorithm simulation complete." + finalSolutionFitness);
	}

	private String[] getParents(BitCodeSolution[] bitCodeSolutions) {
		//population passed is already culled so pick random parents
		String[] parents = new String[2];
		int randomIndex1 = random.nextInt(bitCodeSolutions.length);
		int randomIndex2 = random.nextInt(bitCodeSolutions.length);
		while (randomIndex1 == randomIndex2)
			randomIndex2 = random.nextInt(bitCodeSolutions.length);

		parents[0] = bitCodeSolutions[randomIndex1].getSolution();
		parents[1] = bitCodeSolutions[randomIndex2].getSolution();

		return parents;
	}

	private String crossover(String parentA, String parentB) {
		String offspring = "";
		// use crossover probability.
		int bound = (int) (crossoverChance * 1000);
		int probabilityIndex = random.nextInt(1000);

		if (probabilityIndex < bound) {
			//mate using String.substring() method from a random point
			int crossoverIndex = random.nextInt((parentA.length() - 5) * 10) / 10;
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
			//flip a random bit
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

	private void checkForPlateau(ArrayList<CandidateSolution> plateauSolutions, double currBestSolution) {
		int plateauCounter = 0;
		if (!plateauSolutions.isEmpty()) {
			//check in reverse order
			for (int i = plateauSolutions.size() - 1; i >= 0; i--) {
				double satisfaction = plateauSolutions.get(i).calculateGlobalSatisfaction();
				//if satisfaction repeats minRepetitionsForPlateau times, plateau reached
				if (satisfaction == currBestSolution)
					plateauCounter++;
					//if lower satisfaction found, no need to look further
				else if (satisfaction < currBestSolution)
					break;
			}
		}
		if (plateauCounter == minRepetitionsForPlateau)
			isPlateauReached = true;
	}

	private void sort(BitCodeSolution[] population) {
		Arrays.sort(population, (x, y) -> Double.compare(y.getSatisfaction(), x.getSatisfaction()));
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
				orderOfBitCodes = getNextOrderOfBitCodes(previousOrder);
				bitCodeSolution = generateBitCodeSolution(allBitCodes, orderOfBitCodes);
				bitCodeSolution = mutate(bitCodeSolution);
			}
			population[i] = new BitCodeSolution(bitCodeSolution);
			usedSolutions.add(bitCodeSolution);
		}

		return population;
	}

	private BitCodeSolution[] generateNextPopulation(BitCodeSolution[] previousPopulation) {
		BitCodeSolution[] nextPopulation = new BitCodeSolution[sizeOfPopulation];
		int counter = 0;
		while (counter < sizeOfPopulation) {
			String[] parents = getParents(previousPopulation);
			if (parents[0] == null || parents[1] == null)
				System.out.println(parents[0] + "\n" + parents[1]);
			String offspring = crossover(parents[0], parents[1]);
			if (!offspring.equals("")) {
				offspring = mutate(offspring);
				nextPopulation[counter] = new BitCodeSolution(offspring);
				counter++;
			}
		}
		return nextPopulation;
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
			// randomIndex = random.nextInt(numberOfBitCodes);
			while (usedNumbers.contains(randomIndex))
				randomIndex = random.nextInt(numberOfBitCodes * 10) / 10;
			// randomIndex = random.nextInt(numberOfBitCodes);
			orderOfBitCodes[i] = randomIndex;
			usedNumbers.add(randomIndex);
			if (Common.DEBUG_SHOW_GA) {
				System.out.println(randomIndex);
			}
		}
		//375
		return orderOfBitCodes;
	}

	private int[] getNextOrderOfBitCodes(int[] previousOrderOfBitCodes) {
		int randomIndex = random.nextInt(previousOrderOfBitCodes.length - 1);
		// swap number at randomIndex with the next one.
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
			usedProjects.add(randomProject);
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
