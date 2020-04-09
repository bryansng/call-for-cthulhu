package ie.ucd.objects;

import java.util.ArrayList;
import ie.ucd.Common;
import ie.ucd.interfaces.StudentInterface;

public class Student implements StudentInterface {
	private String firstName;
	private String lastName;
	private Integer id;
	private String stream; // CS or DS.
	private Double gpa;
	private ArrayList<Project> projects = new ArrayList<Project>();
	private ArrayList<Project> preferenceList;

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
	}

	public Double calculateSatisfaction() {
		return preferenceSatisfaction() + gpaSatisfaction() + streamSatisfaction() + assignmentSatisfaction();
	}

	// hard: student assigned exactly one of their preferred projects.
	private Double preferenceSatisfaction() {
		int position = preferenceList.indexOf(projects.get(0));
		return position == -1 ? Common.COST_NOT_ASSIGNED_PREFERENCE_PROJECTS
				: (Common.PROFIT_PROJECT_IN_PREFERENCE_LIST
						- (Common.COST_PER_LOWER_POSITION_PROJECT_IN_PREFERENCE_LIST * position));
	}

	// soft: higher gpa means a greater chance of getting one's preferred projects.
	// soft: students with higher GPAs should tend to get higher preferences than those with lower GPAs
	private Double gpaSatisfaction() {
		int position = preferenceList.indexOf(projects.get(0));
		return position == -1 ? 0.0
				: (gpa / Common.MAX_GPA) * Common.IMPORTANCE_OF_GPA * (preferenceList.size() - position)
						* Common.PROFIT_GPA_MULTIPLIER;
	}

	// hard: student assigned project of the same stream.
	private Double streamSatisfaction() {
		if (projects.get(0).hasCompatibleStream(stream)) {
			return -Common.COST_UNSUITED_STREAM;
		}
		return Common.COST_UNSUITED_STREAM;
	}

	// hard: student assigned exactly one project.
	private Double assignmentSatisfaction() {
		if (projects.size() == 1.0) {
			return -Common.COST_NONE_OR_MULTIPLE_PROJECTS;
		}
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
		return firstName.toLowerCase().contains(searchTerm) || lastName.toLowerCase().contains(searchTerm)
				|| getProject().getResearchActivity().toLowerCase().contains(searchTerm)
				|| stream.toLowerCase().equals(searchTerm) || gpa.toString().equals(searchTerm)
				|| Integer.toString(id).contains(searchTerm);
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

	public Student setProjectAssigned(Project project, Integer index) {
		this.projects.get(0).decrementStudentsAssigned();
		this.projects.get(0).decrementAs1stPreference();
		project.incrementAs1stPreference();
		project.incrementStudentsAssigned();
		this.projects.set(index, project);
		return this;
	}

	public Student setPreferenceList(ArrayList<Project> preferenceList) {
		this.preferenceList = preferenceList;
		return this;
	}

	public Student getCopy() {
		return new Student(firstName, lastName, id, stream, gpa, projects.get(0), preferenceList);
		// return new Student(firstName, lastName, id, stream, gpa, projects.get(0), new ArrayList<Project>(preferenceList)); // preferenceList made a copy.
	}
}
