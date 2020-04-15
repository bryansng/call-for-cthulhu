package ie.ucd.solvers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import ie.ucd.objects.CandidateSolution;
import ie.ucd.Common;
import ie.ucd.objects.Project;
import ie.ucd.objects.Student;
import ie.ucd.ui.solver.SolverPane;

public class GeneticAlgorithm extends Solver {
	private double mutationChance;
	private double crossoverChance;
	private double pickFittestParentsChance;
	private final double fittestParentsIncrementFactor;
	private int numberOfGenerations;
	private int sizeOfPopulation;

	private CandidateSolution startingSolution;
	private CandidateSolution finalSolution;
	private double finalSolutionFitness;

	private final Random random = new Random();
	private SolverPane solverPane;

	public GeneticAlgorithm() {
		this.mutationChance = 0.05;
		this.crossoverChance = 0.4;
		this.numberOfGenerations = 125;
		this.sizeOfPopulation = 70;
		this.pickFittestParentsChance = 0.800;
		this.fittestParentsIncrementFactor = (double) Math
				.round(((1 - pickFittestParentsChance) * 1.500 / numberOfGenerations) * 1000d) / 1000d;
	}

	public GeneticAlgorithm(CandidateSolution startingSolution) {
		this(startingSolution, null);
	}

	public GeneticAlgorithm(CandidateSolution startingSolution, SolverPane solverPane) {
		this(0.05, 0.4, 125, 70, 0.8, startingSolution, solverPane);
	}

	public GeneticAlgorithm(double mutationChance, double crossoverChance, int numberOfGenerations, int sizeOfPopulation,
			double pickFittestParentsChance, CandidateSolution startingSolution, SolverPane solverPane) {
		this.mutationChance = mutationChance;
		this.crossoverChance = crossoverChance;
		this.numberOfGenerations = numberOfGenerations;
		this.sizeOfPopulation = sizeOfPopulation;
		this.pickFittestParentsChance = pickFittestParentsChance;
		this.fittestParentsIncrementFactor = (double) Math
				.round(((1 - pickFittestParentsChance) * 1.500 / numberOfGenerations) * 1000d) / 1000d;
		this.startingSolution = startingSolution;
		this.solverPane = solverPane;
		if (Common.DEBUG_SHOW_GA) {
			System.out.println("Calculated increment factor = " + fittestParentsIncrementFactor);
		}
	}

	public void run() {
		CandidateSolution currSolution = startingSolution;

		// generate bit codes to represent chromosomes.
		ArrayList<String> allBitCodes = generateAllBitCodes(currSolution.getStudents().size());

		// generate population for generation 0.
		ArrayList<String> currPopulation = generateInitialPopulation(allBitCodes);
		ArrayList<String> nextPopulation = new ArrayList<String>();

		ArrayList<Double> populationSatisfactions = null;
		Integer fittestSolutionIndex = null;
		Double fittestSolutionStrength = null;
		String fittestSolution = null;
		for (int i = 1; i <= numberOfGenerations; i++) {
			if (Common.DEBUG_SHOW_GA)
				System.out.println("Creating Generation #" + i);

			// store satisfaction for each bitCodeSolution in a population.
			populationSatisfactions = new ArrayList<Double>();
			for (String bitCodeSolution : currPopulation) {
				currSolution = assignProjectsFromSolution(bitCodeSolution, currSolution);
				// ArrayList<Project> updatedProjects = updateProjects(projects, students.size());
				double populationSatisfaction = currSolution.calculateGlobalSatisfaction();
				if (Common.DEBUG_SHOW_GA)
					System.out.println("populationSatisfaction: " + populationSatisfaction);
				populationSatisfactions.add(populationSatisfaction);
			}

			// get fittest solution from population.
			fittestSolutionIndex = getFittestSolutionIndex(populationSatisfactions);
			fittestSolutionStrength = populationSatisfactions.get(fittestSolutionIndex);
			fittestSolution = currPopulation.get(fittestSolutionIndex);
			if (Common.DEBUG_SHOW_GA) {
				System.out.println("Fittest solution strength: " + fittestSolutionStrength);
			}

			// generate population for next generation.
			while (nextPopulation.size() <= sizeOfPopulation) {
				String[] parents = chooseParents(currPopulation, populationSatisfactions);
				String offspring = crossover(parents[0], parents[1]);
				if (!offspring.equals("")) {
					offspring = mutate(offspring);
					nextPopulation.add(offspring);
				}
			}
			currPopulation.clear();
			currPopulation = new ArrayList<String>(nextPopulation);
			nextPopulation.clear();
			incrementPickFittestParentsChance();
		}

		// get best final solution from final generation.
		finalSolutionFitness = populationSatisfactions.get(fittestSolutionIndex);
		this.finalSolution = assignProjectsFromSolution(fittestSolution, currSolution);
		System.out.println("Genetic Algorithm simulation complete.");
	}

	private String[] chooseParents(ArrayList<String> population, ArrayList<Double> satisfactions) {
		String[] parents = new String[2];
		double max = 1.0, secondMax = 1.0; // arbitrary positive values.

		// convert to array to make process easier.
		int bound = (int) pickFittestParentsChance * 1000;
		int probabilityIndex = random.nextInt(1000);

		if (probabilityIndex < bound) {
			// get two fittest parents.
			for (int i = 0; i < sizeOfPopulation; i++) {
				if (satisfactions.get(i) > max) {
					secondMax = max;
					parents[1] = parents[0];
					max = satisfactions.get(i);
					parents[0] = population.get(i);
				} else if (satisfactions.get(i) > secondMax && satisfactions.get(i) < max) {
					secondMax = satisfactions.get(i);
					parents[1] = population.get(i);
				}
			}
		} else {
			int parent1Index = 0;

			// get the fittest and a random parent to encourage diversity.
			for (int i = 0; i < sizeOfPopulation; i++) {
				if (satisfactions.get(i) > max) {
					max = satisfactions.get(i);
					parents[0] = population.get(i);
					parent1Index = i;
				}
			}

			int parent2Index = random.nextInt(sizeOfPopulation);
			while (parent2Index == parent1Index) {
				parent2Index = random.nextInt(sizeOfPopulation);
			}
			parents[1] = population.get(parent2Index);
		}
		if (Common.DEBUG_SHOW_GA) {
			System.out.println("Parent's strength: " + max + " " + secondMax);
		}
		return parents;
	}

	private String crossover(String parentA, String parentB) {
		String offspring = "";
		// use crossover probability.
		int bound = (int) (crossoverChance * 1000);
		int probabilityIndex = random.nextInt(1000);

		if (probabilityIndex < bound) {
			int crossoverIndex = random.nextInt(parentA.length() * 10) / 10;
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

	private int getFittestSolutionIndex(ArrayList<Double> populationSatisfactions) {
		double max = 0.0;
		int maxIndex = 0;
		int index = 0;
		for (double globalSatisfaction : populationSatisfactions) {
			if (globalSatisfaction > max) {
				max = globalSatisfaction;
				maxIndex = index;
			}
			index++;
		}
		return maxIndex;
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

	private ArrayList<String> generateInitialPopulation(ArrayList<String> allBitCodes) {
		// create the first solution which usually has very good strength.
		int[] orderOfBitCodes = get1stOrderOfBitCodes(allBitCodes.size());
		String bitCodeSolution = generateCandidateSolution(allBitCodes, orderOfBitCodes);
		bitCodeSolution = mutate(bitCodeSolution);

		// add first solution to population.
		ArrayList<String> population = new ArrayList<String>();
		population.add(bitCodeSolution);

		// each following solution builds on the previous one by swapping around.
		// any two random positions in orderOfBitCodes.
		for (int i = 1; i < sizeOfPopulation; i++) {
			int[] previousOrder = orderOfBitCodes;
			orderOfBitCodes = getNextOrderOfBitCodes(previousOrder);
			bitCodeSolution = generateCandidateSolution(allBitCodes, orderOfBitCodes);
			bitCodeSolution = mutate(bitCodeSolution);
			while (population.contains(bitCodeSolution)) {
				orderOfBitCodes = getNextOrderOfBitCodes(orderOfBitCodes);
				bitCodeSolution = generateCandidateSolution(allBitCodes, orderOfBitCodes);
				bitCodeSolution = mutate(bitCodeSolution);
			}
			population.add(bitCodeSolution);
		}

		return population;
	}

	private String generateCandidateSolution(ArrayList<String> allBitCodes, int[] orderOfBitCodes) {
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
	private CandidateSolution assignProjectsFromSolution(String bitCodeSolution, CandidateSolution currSolution) {
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
					// ! doesn't this keep replacing the projectAssigned with a project in the preference list until the end?
					// ! should be once assigned, just break?
					for (Project project : currentPrefList) {
						if (usedProjects.contains(project))
							continue;
						currentStudent.setProjectAssigned(project, 0);
						usedProjects.add(project);
						isAssigned = true;
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
			for (int j = 0; j < solutionInDecimal.length; j++) {
				if (solutionInDecimal[j] > solutionInDecimal[i]) {
					count++;
				}
			}
			ranking[i] = count + 1;
		}
		return ranking;
	}

	/**
	 * This was initially used to update each project with the correct number of students assigned to it.
	 * This was required to ensure satisfaction from the projects side was calculated correctly.
	 * However CandidateSolution was updated to deal with this internally, so this is no longer needed (in theory).
	 */
	private ArrayList<Project> updateProjects(ArrayList<Project> projects, int numberOfProjectsAssigned) {
		// essentially, to calculate fitness, we do not need to know which project was given to whom it only matters how many projects were or were not assigned.
		// ArrayList of Student objects returned by run() already contains the assigned Project object.
		ArrayList<Project> updatedProjects = new ArrayList<Project>();
		int i;
		for (i = 0; i < numberOfProjectsAssigned; i++) {
			Project project = projects.get(i);
			project.setNumStudentsAssigned(1);
			updatedProjects.add(project);
		}
		while (i < projects.size()) {
			Project project = projects.get(i);
			project.setNumStudentsAssigned(0);
			updatedProjects.add(project);
			i++;
		}
		return updatedProjects;
	}

	private void incrementPickFittestParentsChance() {
		pickFittestParentsChance += fittestParentsIncrementFactor;
		if (pickFittestParentsChance > 1.0)
			pickFittestParentsChance = 1.0; // limit probability
	}

	public CandidateSolution getBestSolution() {
		return finalSolution;
	}

	public double getFinalSolutionFitness() {
		return finalSolutionFitness;
	}
}
