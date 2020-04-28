package ie.ucd;

import java.util.ArrayList;
import org.apache.commons.lang3.mutable.MutableInt;
import ie.ucd.objects.Student;
import ie.ucd.io.Parser;
import ie.ucd.objects.CandidateSolution;
import ie.ucd.objects.Project;

public class Settings {
	public static final boolean DEMO_SA_FAST = true;
	public static final boolean DEMO_GA_FAST = true;

	public static boolean enableAnimation = false;
	public static int maximumXAxisTicks = 10000; // higher = UI slower.
	public static int pointsToRemove = maximumXAxisTicks * 20 / 100;

	public static boolean enableGPA = true;
	public static double importanceOfGPA = 1.0; // 0 to 1. This is the value slider changes in the UI.

	public static final Integer DEFAULT_NUMBER_OF_STUDENTS = 500;
	public static MutableInt numberOfStudents = new MutableInt(DEFAULT_NUMBER_OF_STUDENTS);
	public static Parser parser = new Parser();
	public static CandidateSolution setupSolution = new CandidateSolution(numberOfStudents, parser.getStaffMembers(),
			parser.getNames(), null, null);
	public static ArrayList<Student> loadedStudents;
	public static ArrayList<Project> loadedProjects;

	// load button's dialog main directory.
	public static final String DEFAULT_DIRECTORY = System.getProperty("user.dir");
	public static String dialogDirectory = DEFAULT_DIRECTORY;

	// constraints.
	public static int NUM_HARD_CONSTRAINTS = 4;
	public static int NUM_SOFT_CONSTRAINTS = 2;
	public static int TOTAL_CONSTRAINTS = NUM_HARD_CONSTRAINTS + NUM_SOFT_CONSTRAINTS;
	public static final double TOTAL_POINTS = 1.0;
	public static double COST_PER_SOFT_VIOLATION = 0.1 / NUM_SOFT_CONSTRAINTS;
	public static double COST_PER_HARD_VIOLATION = (TOTAL_POINTS - (NUM_SOFT_CONSTRAINTS * COST_PER_SOFT_VIOLATION))
			/ NUM_HARD_CONSTRAINTS;

	public static void prepareSetupSolution() {
		setupSolution = new CandidateSolution(numberOfStudents, parser.getStaffMembers(), parser.getNames(), loadedProjects,
				loadedStudents);
	}

	public static void updateNumEnabledConstraints() {
		NUM_HARD_CONSTRAINTS = 0;
		NUM_SOFT_CONSTRAINTS = 0;

		NUM_HARD_CONSTRAINTS += enableStudentAssignedPreferredProject ? 1 : 0;
		NUM_HARD_CONSTRAINTS += enableSameStream ? 1 : 0;
		NUM_HARD_CONSTRAINTS += enableStudentAssignedOneProject ? 1 : 0;
		NUM_HARD_CONSTRAINTS += enableProjectAssignedToOneStudent ? 1 : 0;

		NUM_SOFT_CONSTRAINTS += enableEquallyDistributedAcrossSupervisors ? 1 : 0;
		NUM_SOFT_CONSTRAINTS += (enableHigherGPAHigherPreferences && enableGPA) ? 1 : 0;

		TOTAL_CONSTRAINTS = NUM_HARD_CONSTRAINTS + NUM_SOFT_CONSTRAINTS;

		COST_PER_SOFT_VIOLATION = NUM_SOFT_CONSTRAINTS > 0 ? (0.1 / NUM_SOFT_CONSTRAINTS) : 0.0;
		COST_PER_HARD_VIOLATION = NUM_HARD_CONSTRAINTS > 0
				? ((TOTAL_POINTS - (NUM_SOFT_CONSTRAINTS * COST_PER_SOFT_VIOLATION)) / NUM_HARD_CONSTRAINTS)
				: 0.0;

		System.out.println("soft: " + COST_PER_SOFT_VIOLATION);
		System.out.println("hard: " + COST_PER_HARD_VIOLATION);
	}

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
	public static final Integer GA_DEFAULT_NUMBER_OF_GENERATION = DEMO_GA_FAST ? 125 : 200;
	public static final Integer GA_DEFAULT_POPULATION_SIZE = DEMO_GA_FAST ? 70 : 1000;
	public static final Double GA_DEFAULT_CROSSOVER_CHANCE = DEMO_GA_FAST ? 0.7 : 0.7;
	public static final Double GA_DEFAULT_MUTATION_CHANCE = DEMO_GA_FAST ? 0.2 : 0.2;
	public static final Double GA_DEFAULT_CULL_CHANCE = DEMO_GA_FAST ? 0.25 : 0.25;
	public static Integer gaNumberOfGeneration = GA_DEFAULT_NUMBER_OF_GENERATION;
	public static Integer gaPopulationSize = GA_DEFAULT_POPULATION_SIZE;
	public static Double gaCrossoverChance = GA_DEFAULT_CROSSOVER_CHANCE;
	public static Double gaMutationChance = GA_DEFAULT_MUTATION_CHANCE;
	public static Double gaCullChance = GA_DEFAULT_CULL_CHANCE;

	// ! consider adding SA cooling type, linear or exponential.

	public static enum Theme {
		ORIGINAL, DARK
	}

	public static boolean enableDarkTheme = true;
}