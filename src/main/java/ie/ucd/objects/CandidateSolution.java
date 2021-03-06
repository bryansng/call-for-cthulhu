package ie.ucd.objects;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import org.apache.commons.lang3.mutable.MutableInt;
import ie.ucd.Common;
import ie.ucd.Settings;
import ie.ucd.exceptions.InsufficientSuitableProjectsException;

public class CandidateSolution {
	private ArrayList<StaffMember> staffMembers;
	private ArrayList<String> names;
	private ArrayList<Project> projects;
	private ArrayList<Student> students;
	private MutableInt numberOfStudents;

	private double numDSStudents = 0.0;
	private double numCSStudents = 0.0;
	private double totalProjectsAssigned = 0.0;

	// constraints.
	// hard.
	private int violationsStudentAssignedPreferredProject;
	private int violationsSameStream;
	private int violationsStudentAssignedOneProject;

	private boolean isViolationProjectCalculated = false;
	private double projectSatisfaction;
	private int violationsProjectAssignedToOneStudent;
	// soft.
	private int violationsEquallyDistributedAcrossSupervisors;
	private int violationsHigherGPAHigherPreferences;

	public CandidateSolution(CandidateSolution other) {
		this(new MutableInt(other.getNumberOfStudents()), other.getStaffMembers(), other.getNames(), other.getProjects(),
				other.getStudents());
	}

	public CandidateSolution(MutableInt numberOfStudents, ArrayList<StaffMember> staffMembers, ArrayList<String> names,
			ArrayList<Project> projects, ArrayList<Student> students) {
		this.numberOfStudents = numberOfStudents;
		this.staffMembers = staffMembers;
		this.names = names;
		this.projects = projects;
		this.students = cloneStudents(students);
	}

	public ArrayList<StaffMember> getStaffMembers() {
		return staffMembers;
	}

	public ArrayList<String> getNames() {
		return names;
	}

	public ArrayList<Project> getProjects() {
		return projects;
	}

	public ArrayList<Student> getStudents() {
		return students;
	}

	public Integer getNumberOfStudents() {
		// if (numberOfStudents.getValue() > students.size()) {
		// 	return numberOfStudents.getValue();
		// }
		return students.size();
	}

	public double getTotalProjectsAssigned() {
		return totalProjectsAssigned;
	}

	public ArrayList<Student> cloneStudents(ArrayList<Student> students) {
		if (students == null)
			return null;

		ArrayList<Student> newStudents = new ArrayList<Student>();
		for (Student student : students) {
			// Student newStudent = student.getCopy();
			// newStudents.add(newStudent);
			// System.out.println(String.format("old (%s): %s, new (%s): %s", System.identityHashCode(student),
			// 		System.identityHashCode(student.getProject()), System.identityHashCode(newStudent),
			// 		System.identityHashCode(newStudent.getProject())));
			newStudents.add(student.getCopy());
		}
		return newStudents;
	}

	public Double calculateGlobalSatisfaction() {
		violationsStudentAssignedPreferredProject = 0;
		violationsSameStream = 0;
		violationsStudentAssignedOneProject = 0;
		violationsEquallyDistributedAcrossSupervisors = 0;
		violationsHigherGPAHigherPreferences = 0;

		Double satisfaction = 0.0;
		for (Student student : students) {
			satisfaction += student.calculateSatisfaction();
			violationsStudentAssignedPreferredProject += student.isPreferenceViolation() ? 1 : 0;
			violationsSameStream += student.isStreamViolation() ? 1 : 0;
			violationsHigherGPAHigherPreferences += student.isGPAViolation() ? 1 : 0;
			violationsStudentAssignedOneProject += student.isAssignmentViolation() ? 1 : 0;
		}
		satisfaction += calculateProjectSatisfactionAndUpdateProjectViolation();
		satisfaction += Settings.enableEquallyDistributedAcrossSupervisors ? projectDistributionToSupervisorsSatisfaction()
				: 0.0;

		// if less than or equal to 1, complement of satisfaction (i.e. 1 / satisfaction) would be an opposite effect, so we limit satisfaction minimum limit to 2.0.
		if (satisfaction <= 1) {
			satisfaction = 2.0;
		}
		return satisfaction;
	}

	// since projects are passed by reference, we need to calculate project satisfaction and violations immediately.
	// if not it will be replaced with future solutions' projects.
	public double calculateProjectSatisfactionAndUpdateProjectViolation() {
		if (!isViolationProjectCalculated) {
			projectSatisfaction = 0.0;
			violationsProjectAssignedToOneStudent = 0;
			if (Settings.enableProjectAssignedToOneStudent) {
				resetProjectsNumStudentsAssigned();
				for (Project project : projects) {
					projectSatisfaction += project.calculateSatisfaction();
					violationsProjectAssignedToOneStudent += project.isAssignmentViolation() ? 1 : 0;
				}
			}
			isViolationProjectCalculated = true;
		}
		return projectSatisfaction;
	}

	// method checks if projects equally distributed across supervisors.
	// if no, has cost per supervisor violation.
	//
	// soft: projects are more-or-less equally distributed across supervisors.
	// ! dont have to calculate every time
	private Double projectDistributionToSupervisorsSatisfaction() {
		for (StaffMember staff : staffMembers) {
			if (!(staff.getNumberActivitiesUsed() <= (Common.numAvgProjectsProposed + 2))) {
				violationsEquallyDistributedAcrossSupervisors += 1;
			}
		}
		return violationsEquallyDistributedAcrossSupervisors * Common.COST_UNEQUAL_PROJECT_DISTRIBUTION_TO_SUPERVISOR;
	}

	public String toStringStudents() {
		String res = "";
		for (Student student : students) {
			res += student + "\n";
		}
		return res;
	}

	public ArrayList<Project> generateProjects() {
		projects = new ArrayList<Project>();

		HashSet<String> projectsUsed = new HashSet<String>();

		for (StaffMember staffMember : staffMembers) {
			staffMember.resetResearchActivitiesUsed();
		}

		int numProjects = (int) Math.round(numberOfStudents.getValue() * 1.0 / 2);
		for (int i = 0; i < Common.numAvgProjectsProposed * numProjects;) {
			int randInt = new Random().nextInt(staffMembers.size());
			while (staffMembers.get(randInt).isAllActivitiesUsed()) {
				randInt = new Random().nextInt(staffMembers.size());
			}

			// if project already proposed by another staff member.
			Project potentialProject = staffMembers.get(randInt).getProject();
			if (projectsUsed.contains(potentialProject.getResearchActivity())) {
				staffMembers.get(randInt).setProjectUnused(potentialProject.getResearchActivity());
				continue;
			}

			projectsUsed.add(potentialProject.getResearchActivity());
			projects.add(potentialProject);
			i++;
		}

		if (!isEvenProjectStreamAllocation() && numProjects > 5) {
			return generateProjects();
		}
		return projects;
	}

	public ArrayList<Student> generateStudents() throws InsufficientSuitableProjectsException {
		HashSet<Integer> usedStudentIDs = new HashSet<Integer>();
		students = new ArrayList<Student>();
		numDSStudents = 0.0;
		numCSStudents = 0.0;
		totalProjectsAssigned = 0.0;

		resetProjectsCounters();

		for (int i = 0; i < numberOfStudents.getValue(); i++) {
			Integer randomInt = new Random().nextInt(names.size());
			String[] name = names.get(randomInt).split(",")[1].split(" ");
			String firstName = name[0];
			String lastName = "";
			if (name.length > 1) {
				lastName = name[name.length - 1];
			}

			Integer randomId = generateStudentId();
			while (usedStudentIDs.contains(randomId)) {
				randomId = generateStudentId();
			}
			String stream = generateStudentStream();
			usedStudentIDs.add(randomId);

			students.add(new Student(firstName, lastName, randomId, stream, Common.rand.nextDouble() * 4.2,
					generatePreferenceList(stream, randomId)));
		}

		if (!isEvenStudentStreamAllocation() && numberOfStudents.getValue() > 0) {
			return generateStudents();
		}
		return students;
	}

	private Integer generateStudentId() {
		return new Random().nextInt(90000000) + 10000000; // 1000 0000 - 9999 9999.
	}

	// checks for 1st preferences.
	private ArrayList<Project> generatePreferenceList(String stream, Integer randomId)
			throws InsufficientSuitableProjectsException {
		int noSuitableProjectsCounter = 0;
		ArrayList<Project> list = new ArrayList<Project>();
		HashSet<Integer> usedIndex = new HashSet<Integer>();

		// assuming impossible to run out of projects to give as 1st preference since projects.size() always > numOfStudents.
		for (int i = 0; i < 10;) {
			int randomIndex = new Random().nextInt(projects.size());
			Project project = projects.get(randomIndex);
			if (!usedIndex.contains(randomIndex) && project.hasCompatibleStream(stream)
					&& project.doesStudentPreferProject()) {
				project.incrementAsPreference();
				if (i == 0) {
					project.incrementAs1stPreference();
					project.incrementStudentsAssigned();
					project.setIsGivenAs1stPreference(true);
				}

				usedIndex.add(randomIndex);
				list.add(project);
				i++;
				totalProjectsAssigned++;
				noSuitableProjectsCounter = 0;
			} else {
				noSuitableProjectsCounter += 1;
			}

			if (noSuitableProjectsCounter >= Common.MAX_NO_SUITABLE_PROJECTS) {
				throw new InsufficientSuitableProjectsException(
						"Insufficient suitable projects (i.e. incompatible stream, etc) during generation of student preference list.");
			}
		}

		return list;
	}

	// checks for 1st preferences.
	private ArrayList<Project> generatePreferenceListV1(String stream, Integer randomId) {
		ArrayList<Project> list = new ArrayList<Project>();
		HashSet<Integer> usedIndex = new HashSet<Integer>();

		// resetProjectsCounters();

		// assuming impossible to run out of projects to give as 1st preference since projects.size() always > numOfStudents.
		for (int i = 0; i < 10;) {
			int randomIndex = new Random().nextInt(projects.size());
			Project project = projects.get(randomIndex);
			if (!usedIndex.contains(randomIndex) && project.hasCompatibleStream(stream) && project.doesStudentPreferProject()
					&& (i != 0 || (!project.isGivenAs1stPreference() && i == 0))) {
				project.incrementAsPreference();
				if (i == 0) {
					project.incrementAs1stPreference();
					project.incrementStudentsAssigned();
					project.setIsGivenAs1stPreference(true);
				}

				usedIndex.add(randomIndex);
				list.add(project);
				i++;
				totalProjectsAssigned++;
			}
		}

		return list;
	}

	private void resetProjectsCounters() {
		for (Project project : projects) {
			project.resetCounters();
		}
	}

	// only used during calculateSatisfaction.
	public void resetProjectsNumStudentsAssigned() {
		// resets all to zero.
		for (Project project : projects) {
			project.setNumStudentsAssigned(0.0);
		}

		// increment when a student has it.
		for (Student student : students) {
			Project projectAssigned = student.getProject();
			projectAssigned.incrementStudentsAssigned();
		}
	}

	private String generateStudentStream() {
		int randomInt = new Random().nextInt(5) + 1; // 1-5

		// 40% is DS, 2/5.
		if (randomInt == 4 || randomInt == 5) {
			numDSStudents += 1.0;
			return "DS";
		}
		numCSStudents += 1.0;
		return "CS"; // 60% is CS, 3/5.
	}

	public String CSDSPercentage() {
		return "Percentage CS: " + getCSPercentage() + "\nPercentage DS: " + getDSPercentage() + "\n";
	}

	private String getCSPercentage() {
		return formatPercentage(numCSStudents / numberOfStudents.getValue() * 100.0);
	}

	private String getDSPercentage() {
		return formatPercentage(numDSStudents / numberOfStudents.getValue() * 100.0);
	}

	public String ProjectDistributionPercentage() {
		String res = "\n\nPercentage project distribution:\n";
		for (Project project : projects) {
			res += project.getResearchActivity() + " - " + project.getStream() + " - " + project.getPreferredProbability()
					+ " - " + formatPercentage(project.getNumAsPreference() / totalProjectsAssigned * 100) + "\n";
		}
		return res;
	}

	public String Project1stPreferencePercentage() {
		String res = "\n\nPercentage project as 1st Preference:\n";
		for (Project project : projects) {
			res += project.getResearchActivity() + " - " + project.getStream() + " - " + project.getPreferredProbability()
					+ " - " + formatPercentage(project.getNumAs1stPreference() / numberOfStudents.getValue() * 100) + "\n";
		}
		return res;
	}

	public String formatPercentage(double percentage) {
		return String.format("%.2f%%", percentage);
	}

	private Boolean isEvenProjectStreamAllocation() {
		int numDS = 0;
		for (Project project : projects) {
			if (project.getStream().equals("DS")) {
				numDS += 1;
			}
		}
		double percentDS = numDS * 1.0 / projects.size() * 100;
		// System.out.println("Project percent: " + percentDS);
		return percentDS >= 40 && percentDS <= 60; // 50 +- 10
	}

	private Boolean isEvenStudentStreamAllocation() {
		double percentDS = numDSStudents * 1.0 / students.size() * 100;
		// System.out.println("Student percent: " + percentDS);
		return percentDS >= 35 && percentDS <= 45; // 40 +- 5
	}

	public int getViolationsStudentAssignedPreferredProject() {
		return violationsStudentAssignedPreferredProject;
	}

	public int getViolationsSameStream() {
		return violationsSameStream;
	}

	public int getViolationsStudentAssignedOneProject() {
		return violationsStudentAssignedOneProject;
	}

	public int getViolationsProjectAssignedToOneStudent() {
		return violationsProjectAssignedToOneStudent;
	}

	public int getViolationsEquallyDistributedAcrossSupervisors() {
		return violationsEquallyDistributedAcrossSupervisors;
	}

	public int getViolationsHigherGPAHigherPreferences() {
		return violationsHigherGPAHigherPreferences;
	}
}