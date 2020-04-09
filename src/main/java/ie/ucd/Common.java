package ie.ucd;

import java.util.Random;

import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;

public class Common {
	public static final boolean CHART_ENABLE_TRUNCATE = true;

	public static enum SolverType {
		SimulatedAnnealing, GeneticAlgorithm
	}

	public static enum SheetType {
		Student, Project
	}

	public static enum SARandomMoveType {
		FROM_STUDENT_PREFERENCE_LIST, SWAP_FROM_TWO_STUDENTS_PREFERENCE_LIST, SWAP_FROM_TWO_STUDENTS_ASSIGNED_PROJECTS
	}

	public static final boolean DEBUG_SHOW_SA = false; // SA debug verbosity.
	public static final boolean DEBUG_SHOW_ENERGIES = false;
	public static SARandomMoveType SA_RANDOM_MOVE_TYPE = SARandomMoveType.SWAP_FROM_TWO_STUDENTS_PREFERENCE_LIST; // SA debug verbosity.

	// the below are used during calculating of global/local satisfaction.
	public static final double MAX_GPA = 4.2;
	public static final double PROFIT_GPA_MULTIPLIER = 5.0;
	public static final double IMPORTANCE_OF_GPA = 1.0; // 0 to 1. This is the value slider changes in the UI.
	public static final double PROFIT_PROJECT_IN_PREFERENCE_LIST = 20.0;
	public static final double COST_PER_LOWER_POSITION_PROJECT_IN_PREFERENCE_LIST = -2.0;
	public static final double COST_NOT_ASSIGNED_PREFERENCE_PROJECTS = -5.0;
	public static final double COST_UNSUITED_STREAM = -5.0;
	public static final double COST_NONE_OR_MULTIPLE_PROJECTS = -2.0;
	public static final double COST_UNEQUAL_PROJECT_DISTRIBUTION_TO_SUPERVISOR = -5.0;

	// the below are used during generation of our random candidate solution.
	public static Random rand = new Random();
	public static int numAvgProjectsProposed = 3;
	public static AbstractRealDistribution distr = new NormalDistribution();
	private static double gaussianLimit = 2.5;

	// https://www.javamex.com/tutorials/random_numbers/gaussian_distribution_2.shtml
	public static double getProbability() {
		double aDouble = rand.nextGaussian();
		while (!(aDouble >= -gaussianLimit && aDouble <= gaussianLimit)) {
			aDouble = rand.nextGaussian();
		}
		return distr.density(rand.nextGaussian());
	}

	public static double getMax() {
		return distr.density(0.0);
	}

	public static double getMin() {
		return distr.density(gaussianLimit);
	}

	public static double getRange() {
		return getMax() - getMin();
	}
}