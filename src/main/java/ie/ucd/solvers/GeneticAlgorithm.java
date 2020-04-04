package ie.ucd.solvers;

import java.util.ArrayList;
import ie.ucd.objects.CandidateSolution;

public class GeneticAlgorithm {
	private int numOfGenerations;
	private int populationSize;
	private double mutationProbability;

	public GeneticAlgorithm() {
		this(100, 100, 0.3);
	}

	public GeneticAlgorithm(int numOfGenerations, int populationSize, double mutationProbability) {
		this.numOfGenerations = numOfGenerations;
		this.populationSize = populationSize;
		this.mutationProbability = mutationProbability;
	}

	public CandidateSolution run(CandidateSolution solution) {
		int i;
		for (i = 0; i < numOfGenerations; i++) {

		}
		return null;
	}

	// handle crossover

	// handle mutation

	// generate bit codes.

	// generate population.
	private ArrayList<CandidateSolution> generatePopulation(CandidateSolution solution) {
		ArrayList<CandidateSolution> population = new ArrayList<CandidateSolution>();
		population.add(solution);
		for (int i = 1; i < populationSize; i++) {
			population.add(generateChromosome(solution));
		}
		return population;
	}

	// generate chromosomes
	private CandidateSolution generateChromosome(CandidateSolution solution) {
		return new CandidateSolution(solution);
	}
}
