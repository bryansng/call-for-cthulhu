package ie.ucd;

public class Settings {
	public static boolean enableAnimation = false;

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
}