package ie.ucd;

public class Settings {
	public static final boolean DEMO_SA_FAST = true;
	public static final boolean DEMO_GA_FAST = true;

	public static boolean enableAnimation = true;
	public static int maximumXAxisTicks = 10000; // higher = UI slower.
	public static int pointsToRemove = maximumXAxisTicks * 20 / 100;

	public static double importanceOfGPA = 1.0; // 0 to 1. This is the value slider changes in the UI.

	// constraints.
	public static final int NUM_HARD_CONSTRAINTS = 4;
	public static final int NUM_SOFT_CONSTRAINTS = 2;
	public static final int TOTAL_CONSTRAINTS = NUM_HARD_CONSTRAINTS + NUM_SOFT_CONSTRAINTS;
	public static final double TOTAL_POINTS = 1.0;
	public static final double COST_PER_SOFT_VIOLATION = 0.05;
	public static final double COST_PER_HARD_VIOLATION = (TOTAL_POINTS - (NUM_SOFT_CONSTRAINTS * COST_PER_SOFT_VIOLATION))
			/ NUM_HARD_CONSTRAINTS;
	// hard.
	public static boolean enableStudentAssignedPreferredProject = true;
	public static boolean enableSameStream = true;
	public static boolean enableStudentAssignedOneProject = true;
	public static boolean enableProjectAssignedToOneStudent = true;
	// soft.
	public static boolean enableEquallyDistributedAcrossSupervisors = true;
	public static boolean enableHigherGPAHigherPreferences = true;

	public static enum SARandomMoveType {
		FROM_STUDENT_PREFERENCE_LIST, SWAP_FROM_TWO_STUDENTS_PREFERENCE_LIST, SWAP_FROM_TWO_STUDENTS_ASSIGNED_PROJECTS
	}

	public static SARandomMoveType SA_RANDOM_MOVE_TYPE = SARandomMoveType.SWAP_FROM_TWO_STUDENTS_PREFERENCE_LIST;

	// SA parameters.
	public static final Double SA_DEFAULT_START_TEMPERATURE = DEMO_SA_FAST ? 100.0 : 100.0;
	public static final Double SA_DEFAULT_COOLING_RATE = DEMO_SA_FAST ? 0.001 : 0.0001;
	public static final Double SA_DEFAULT_MIN_TEMPERATURE = DEMO_SA_FAST ? 0.001 : 0.000000000000001;
	public static final Double SA_DEFAULT_MAX_ITERATION = DEMO_SA_FAST ? 50000.0 : 10000000.0;
	public static Double saStartTemperature = SA_DEFAULT_START_TEMPERATURE;
	public static Double saCoolingRate = SA_DEFAULT_COOLING_RATE;
	public static Double saMinTemperature = SA_DEFAULT_MIN_TEMPERATURE;
	public static Double saMaxIteration = SA_DEFAULT_MAX_ITERATION;

	// GA parameters.
	public static final Integer GA_DEFAULT_NUMBER_OF_GENERATION = DEMO_GA_FAST ? 125 : 125;
	public static final Integer GA_DEFAULT_POPULATION_SIZE = DEMO_GA_FAST ? 70 : 70;
	public static final Double GA_DEFAULT_CROSSOVER_CHANCE = DEMO_GA_FAST ? 0.4 : 0.4;
	public static final Double GA_DEFAULT_MUTATION_CHANCE = DEMO_GA_FAST ? 0.05 : 0.05;
	public static final Double GA_DEFAULT_PICK_FITTEST_PARENTS_CHANCE = DEMO_GA_FAST ? 0.8 : 0.8;
	public static Integer gaNumberOfGeneration = GA_DEFAULT_NUMBER_OF_GENERATION;
	public static Integer gaPopulationSize = GA_DEFAULT_POPULATION_SIZE;
	public static Double gaCrossoverChance = GA_DEFAULT_CROSSOVER_CHANCE;
	public static Double gaMutationChance = GA_DEFAULT_MUTATION_CHANCE;
	public static Double gaPickFittestParentsChance = GA_DEFAULT_PICK_FITTEST_PARENTS_CHANCE;

	// ! consider adding SA cooling type, linear or exponential.
}