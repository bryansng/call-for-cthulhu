package ie.ucd;

public class Settings {
	public static double importanceOfGPA = 1.0; // 0 to 1. This is the value slider changes in the UI.

	// constraints.
	// hard.
	public static boolean enableStudentAssignedPreferredProject = true;
	public static boolean enableSameStream = true;
	public static boolean enableStudentAssignedOneProject = true;
	public static boolean enableProjectAssignedToOneStudent = true;
	// soft.
	public static boolean enableEquallyDistributedAcrossSupervisors = true;
	public static boolean enableHigherGPAHigherPreferences = true;
}