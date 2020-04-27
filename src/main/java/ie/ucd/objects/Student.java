package ie.ucd.objects;

import java.util.ArrayList;
import ie.ucd.Common;
import ie.ucd.Settings;
import ie.ucd.interfaces.SearchMatchable;
import ie.ucd.interfaces.StudentInterface;

public class Student implements StudentInterface, SearchMatchable {
	private String firstName;
	private String lastName;
	private Integer id;
	private String stream; // CS or DS.
	private Double gpa;
	private ArrayList<Project> projects = new ArrayList<Project>();
	private ArrayList<Project> preferenceList;

	// violations.
	private boolean preferenceViolation;
	private boolean gpaViolation;
	private boolean streamViolation;
	private boolean assignmentViolation;
	private boolean calculatedViolation;

	public Student(String firstName, String lastName, Integer id, String stream, Double gpa,
			ArrayList<Project> preferenceList) {
		this(firstName, lastName, id, stream, gpa, preferenceList.get(0), preferenceList);
	}

	public Student(String firstName, String lastName, Integer id, String stream, Double gpa, Project projectAssigned,
			ArrayList<Project> preferenceList) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.id = id;
		this.stream = stream;
		this.gpa = gpa;
		this.projects.add(projectAssigned);
		this.preferenceList = preferenceList;
		if (Common.DEBUG_SHOW_PROJECT_NUM_STUDENT_ASSIGNED) {
			System.out.println(String.format("\nproject address: %s", System.identityHashCode(projectAssigned)));
			System.out.println("project assigned to initialized student: " + projects.get(0).getNumStudentsAssigned() + " "
					+ projectAssigned.getNumStudentsAssigned());
		}
	}

	public Double calculateSatisfaction() {
		preferenceViolation = false;
		gpaViolation = false;
		streamViolation = false;
		assignmentViolation = false;
		calculatedViolation = false;

		Double satisfaction = 0.0;
		satisfaction += Settings.enableStudentAssignedPreferredProject ? preferenceSatisfaction() : 0.0;
		satisfaction += Settings.enableSameStream ? streamSatisfaction() : 0.0;
		satisfaction += Settings.enableStudentAssignedOneProject ? assignmentSatisfaction() : 0.0;
		satisfaction += (Settings.enableHigherGPAHigherPreferences && Settings.enableGPA && gpa != null) ? gpaSatisfaction()
				: 0.0;

		calculatedViolation = true;
		return satisfaction;
	}

	// hard: student assigned exactly one of their preferred projects.
	private Double preferenceSatisfaction() {
		int position = preferenceList.indexOf(projects.get(0));

		if (position == -1) {
			preferenceViolation = true;
			return Common.COST_NOT_ASSIGNED_PREFERENCE_PROJECTS;
		}
		return (Common.PROFIT_PROJECT_IN_PREFERENCE_LIST
				- (Common.COST_PER_LOWER_POSITION_PROJECT_IN_PREFERENCE_LIST * position));
	}

	// soft: higher gpa means a greater chance of getting one's preferred projects.
	// soft: students with higher GPAs should tend to get higher preferences than those with lower GPAs.
	private Double gpaSatisfaction() {
		int position = preferenceList.indexOf(projects.get(0));

		if (position == -1) {
			return 0.0;
		}

		// violation if,
		// high gpa, but low preference.
		// low gpa, but high preference.
		// so we if within lower and upper limit, if it is, no violation.
		if (!isInTolerableGPAPreferenceLimit(position)) {
			gpaViolation = true;
		}

		return (gpa / Common.MAX_GPA) * Settings.importanceOfGPA * (preferenceList.size() - position)
				* Common.PROFIT_GPA_MULTIPLIER;
	}

	private boolean isInTolerableGPAPreferenceLimit(int projectPositionInPreferenceList) {
		int midIndex = preferenceList.size() - (new Double(gpa / Common.MAX_GPA * preferenceList.size()).intValue());
		int lower = midIndex - Common.GPA_VIOLATION_INDEX_LIMIT;
		int upper = midIndex + Common.GPA_VIOLATION_INDEX_LIMIT;
		return projectPositionInPreferenceList >= lower && projectPositionInPreferenceList <= upper;
	}

	// hard: student assigned project of the same stream.
	private Double streamSatisfaction() {
		if (projects.get(0).hasCompatibleStream(stream)) {
			return -Common.COST_UNSUITED_STREAM;
		}
		streamViolation = true;
		return Common.COST_UNSUITED_STREAM;
	}

	// hard: student assigned exactly one project.
	private Double assignmentSatisfaction() {
		if (projects.size() == 1.0) {
			return -Common.COST_NONE_OR_MULTIPLE_PROJECTS;
		}
		assignmentViolation = true;
		return Common.COST_NONE_OR_MULTIPLE_PROJECTS * projects.size();
	}

	public String toString() {
		String buf = "\t";
		return firstName + " " + lastName + buf + id + buf + this.projects.get(0).getResearchActivity() + buf + stream;
	}

	public String toStringPreferenceList() {
		String buf = "\t\t";

		String projects = "";
		int count = 0;
		for (Project project : preferenceList) {
			projects += project.getResearchActivity();
			count++;

			if (count < preferenceList.size()) {
				projects += ", ";
			}
		}

		return firstName + " " + lastName + buf + id + buf + this.projects.get(0) + buf + stream + buf + projects;
	}

	public boolean matchSearchTerm(String searchTerm) {
		searchTerm = searchTerm.toLowerCase();
		try {
			return firstName.toLowerCase().contains(searchTerm) || lastName.toLowerCase().contains(searchTerm)
					|| getProject().getResearchActivity().toLowerCase().contains(searchTerm)
					|| stream.toLowerCase().equals(searchTerm) || gpa.toString().equals(searchTerm)
					|| Integer.toString(id).contains(searchTerm);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean isInPreferenceList(String searchTerm) {
		boolean isInPreferenceList = false;
		for (Project p : preferenceList) {
			if (p.getResearchActivity().toLowerCase().contains(searchTerm)) {
				isInPreferenceList = true;
				break;
			}
		}
		return isInPreferenceList;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public Integer getId() {
		return id;
	}

	public String getStream() {
		return stream;
	}

	public Double getGpa() {
		return gpa;
	}

	public Project getProject() {
		return getProjectAssigned(0);
	}

	public ArrayList<Project> getProjectAssigned() {
		return projects;
	}

	public Project getProjectAssigned(Integer index) {
		return projects.get(index);
	}

	public ArrayList<Project> getPreferenceList() {
		return preferenceList;
	}

	public boolean isPreferenceViolation() {
		return preferenceViolation;
	}

	public boolean isStreamViolation() {
		return streamViolation;
	}

	public boolean isGPAViolation() {
		return gpaViolation;
	}

	public boolean isAssignmentViolation() {
		return assignmentViolation;
	}

	public boolean isViolationCalculated() {
		return calculatedViolation;
	}

	public boolean hasHardConstraintViolation() {
		return preferenceViolation || streamViolation || assignmentViolation;
	}

	public Student setFirstName(String firstName) {
		this.firstName = firstName;
		return this;
	}

	public Student setLastName(String lastName) {
		this.lastName = lastName;
		return this;
	}

	public Student setId(Integer id) {
		this.id = id;
		return this;
	}

	public Student setStream(String stream) {
		this.stream = stream;
		return this;
	}

	public Student setGPA(Double gpa) {
		this.gpa = gpa;
		return this;
	}

	public Student setProjectsAssigned(ArrayList<Project> projects) {
		this.projects = projects;
		return this;
	}

	// more suited for SA then GA.
	public Student setProjectAssigned(Project project, Integer index) {
		if (Common.DEBUG_SHOW_PROJECT_NUM_STUDENT_ASSIGNED) {
			System.out.println(String.format("\nstudent %s previous p %s, new p %s", System.identityHashCode(this),
					System.identityHashCode(this.projects.get(index)), System.identityHashCode(project)));
			System.out.println(String.format("before math: previous %f, new %f",
					this.projects.get(index).getNumStudentsAssigned(), project.getNumStudentsAssigned()));
			// this.projects.get(index).decrementStudentsAssigned();
			// this.projects.get(index).decrementAs1stPreference();
			// project.incrementAs1stPreference();
			// project.incrementStudentsAssigned();
			System.out.println(String.format("after math: previous %f, new %f",
					this.projects.get(index).getNumStudentsAssigned(), project.getNumStudentsAssigned()));
			if (this.projects.get(index).getNumStudentsAssigned() == 0)
				System.out.println(System.identityHashCode(this.projects.get(index)) + " "
						+ this.projects.get(index).getNumStudentsAssigned());
		}
		this.projects.set(index, project);
		return this;
	}

	public Student setPreferenceList(ArrayList<Project> preferenceList) {
		this.preferenceList = preferenceList;
		return this;
	}

	public Student getCopy() {
		if (Common.DEBUG_SHOW_PROJECT_NUM_STUDENT_ASSIGNED)
			System.out.println("getCopy's project numStudentAssigned check: " + projects.get(0).getNumStudentsAssigned());
		return new Student(firstName, lastName, id, stream, gpa, projects.get(0), preferenceList);

		// return new Student(firstName, lastName, id, stream, gpa, projects.get(0), new ArrayList<Project>(preferenceList)); // preferenceList made a copy.
	}
}
