package ie.ucd.solvers;

import java.util.ArrayList;

import ie.ucd.interfaces.Solver;
import ie.ucd.objects.CandidateSolution;

public class GeneticAlgorithm implements Solver {
	private CandidateSolution startingSolution;
	private int numOfGenerations;
	private int populationSize;
	private double mutationProbability;

	public GeneticAlgorithm(CandidateSolution startingSolution) {
		this(100, 100, 0.3, startingSolution);
	}

	public GeneticAlgorithm(int numOfGenerations, int populationSize, double mutationProbability,
			CandidateSolution startingSolution) {
		this.numOfGenerations = numOfGenerations;
		this.populationSize = populationSize;
		this.mutationProbability = mutationProbability;
		this.startingSolution = startingSolution;
	}

	public CandidateSolution run() {
		int i;
		for (i = 0; i < numOfGenerations; i++) {

		}
		return null;
	}

	// handle crossover

	// handle mutation

	// generate bit codes.

	// generate population.
	private ArrayList<CandidateSolution> generatePopulation() {
		ArrayList<CandidateSolution> population = new ArrayList<CandidateSolution>();
		population.add(startingSolution);
		for (int i = 1; i < populationSize; i++) {
			population.add(generateChromosome(startingSolution));
		}
		return population;
	}

	// generate chromosomes
	private CandidateSolution generateChromosome(CandidateSolution solution) {
		return new CandidateSolution(solution);
	}
}
