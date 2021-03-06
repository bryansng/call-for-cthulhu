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

	// NavigationPane on typed parameter debug verbosity.
	public static final boolean DEBUG_SHOW_PARAMETER_CHANGE_ON_TYPE = false;

	// ControlButton will change to using solvers' internal parameter configs.
	public static final boolean IS_DEBUGGING_SOLVERS = false;

	// Used during load and save of projectsCSV500.
	// some staff names have unicode characters, which were not taken into account.
	public static final boolean DEBUG_IO_UNICODE = false;

	// SA debug verbosity.
	public static final boolean DEBUG_SHOW_SA = false;
	public static final boolean DEBUG_SHOW_ENERGIES = false;
	public static final boolean DEBUG_SHOW_PROJECT_NUM_STUDENT_ASSIGNED = false;

	// GA debug verbosity.
	public static final boolean DEBUG_SHOW_GA = false;

	// keep tracks if projects are loaded/generated.
	public static boolean isProjectsPopulated = false;
	public static final boolean DEBUG_SHOW_IS_PROJECTS_POPULATED = false;

	// keep tracks if students loaded have stream column information.
	public static boolean doesLoadedFileHaveStream = false;

	// the below are used during calculating of global/local satisfaction.
	public static final double MAX_GPA = 4.2;
	public static final double PROFIT_GPA_MULTIPLIER = 5.0;
	public static final double PROFIT_PROJECT_IN_PREFERENCE_LIST = 20.0;
	public static final int GPA_VIOLATION_INDEX_LIMIT = 2;
	public static final double COST_PER_LOWER_POSITION_PROJECT_IN_PREFERENCE_LIST = -2.0;
	public static final double COST_NOT_ASSIGNED_PREFERENCE_PROJECTS = -5.0;
	public static final double COST_UNSUITED_STREAM = -5.0;
	public static final double COST_NONE_OR_MULTIPLE_PROJECTS = -2.0;
	public static final double COST_UNEQUAL_PROJECT_DISTRIBUTION_TO_SUPERVISOR = -5.0;

	// generateStudents' generatePreferenceList' no suitable projects MAX limit.
	public static final int MAX_NO_SUITABLE_PROJECTS = 2000;

	// the below are used during generation of our random candidate solution.
	public static Random rand = new Random();
	public static int numAvgProjectsProposed = 3;
	public static AbstractRealDistribution distr = new NormalDistribution();
	private static double gaussianLimit = 2.5;

	// used in Genetic Algorithm.
	public static final int MAX_ARRAY_SIZE = 1500;

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