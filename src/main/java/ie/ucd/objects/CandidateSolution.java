package ie.ucd.objects;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import ie.ucd.Common;

public class CandidateSolution {
	private ArrayList<StaffMember> staffMembers;
	private ArrayList<String> names;
	private ArrayList<Project> projects;
	private ArrayList<Student> students;
	private int numberOfStudents;

	private double numDSStudents = 0.0;
	private double numCSStudents = 0.0;
	private double totalProjectsAssigned = 0.0;

	public CandidateSolution(CandidateSolution other) {
		this(other.getNumberOfStudents(), other.getStaffMembers(), other.getNames(), other.getProjects(),
				other.getStudents());
	}

	public CandidateSolution(int numberOfStudents, ArrayList<StaffMember> staffMembers, ArrayList<String> names,
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

	public int getNumberOfStudents() {
		return numberOfStudents;
	}

	public double getTotalProjectsAssigned() {
		return totalProjectsAssigned;
	}

	public ArrayList<Student> cloneStudents(ArrayList<Student> students) {
		if (students == null)
			return null;

		ArrayList<Student> newStudents = new ArrayList<Student>();
		for (Student student : students) {
			newStudents.add(student.getCopy());
		}
		return newStudents;
	}

	public Double calculateGlobalSatisfaction() {
		Double satisfaction = 0.0;
		for (Student student : students) {
			satisfaction += student.calculateSatisfaction();
		}
		for (Project project : projects) {
			satisfaction += project.calculateSatisfaction();
		}
		satisfaction += projectDistributionToSupervisorsSatisfaction();

		// if less than or equal to 1, complement of satisfaction (i.e. 1 / satisfaction) would be an opposite effect, so we limit satisfaction minimum limit to 2.0.
		if (satisfaction <= 1) {
			satisfaction = 2.0;
		}
		return satisfaction;
	}

	// method checks if projects equally distributed across supervisors.
	// if no, has cost per supervisor violation.
	//
	// soft: projects are more-or-less equally distributed across supervisors.
	// ! dont have to calculate every time
	private Double projectDistributionToSupervisorsSatisfaction() {
		double numViolations = 0.0;
		for (StaffMember staff : staffMembers) {
			if (staff.getNumberActivitiesUsed() <= Common.numAvgProjectsProposed + 2) {
				numViolations += 1.0;
			}
		}
		return numViolations * Common.COST_UNEQUAL_PROJECT_DISTRIBUTION_TO_SUPERVISOR;
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

		int numProjects = numberOfStudents / 2;
		for (int i = 0; i < Common.numAvgProjectsProposed * numProjects; i++) {
			int randInt = new Random().nextInt(staffMembers.size());
			while (staffMembers.get(randInt).isAllActivitiesUsed()) {
				randInt = new Random().nextInt(staffMembers.size());
			}

			projects.add(staffMembers.get(randInt).getProject());
		}

		if (!isEvenProjectStreamAllocation()) {
			return generateProjects();
		}
		return projects;
	}

	public ArrayList<Student> generateStudents() throws IOException {
		HashSet<Integer> usedStudentIDs = new HashSet<Integer>();
		students = new ArrayList<Student>();
		numDSStudents = 0.0;
		numCSStudents = 0.0;
		totalProjectsAssigned = 0.0;

		for (int i = 0; i < numberOfStudents; i++) {
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
			students.add(new Student(firstName, lastName, randomId, stream, 0.0, generatePreferenceList(stream, randomId)));
		}

		if (!isEvenStudentStreamAllocation()) {
			return generateStudents();
		}
		return students;
	}

	private Integer generateStudentId() {
		return new Random().nextInt(90000000) + 10000000; // 1000 0000 - 9999 9999.
	}

	// checks for 1st preferences.
	private ArrayList<Project> generatePreferenceList(String stream, Integer randomId) {
		ArrayList<Project> list = new ArrayList<Project>();
		HashSet<Integer> usedIndex = new HashSet<Integer>();
		resetProjectsCounters();

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
		return formatPercentage(numCSStudents / numberOfStudents * 100.0);
	}

	private String getDSPercentage() {
		return formatPercentage(numDSStudents / numberOfStudents * 100.0);
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
					+ " - " + formatPercentage(project.getNumAs1stPreference() / numberOfStudents * 100) + "\n";
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
}