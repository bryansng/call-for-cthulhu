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
		return preferenceSatisfaction() + streamSatisfaction() + assignmentSatisfaction();
	}

	private Double preferenceSatisfaction() {
		int position = preferenceList.indexOf(projects.get(0));
		return position == -1 ? Common.COST_NOT_ASSIGNED_PREFERENCE_PROJECTS : 10.0 - position;
	}

	private Double streamSatisfaction() {
		if (projects.get(0).hasCompatibleStream(stream)) {
			return -Common.COST_UNSUITED_STREAM;
		}
		return Common.COST_UNSUITED_STREAM;
	}

	private Double assignmentSatisfaction() {
		if (projects.size() == 1.0) {
			return -Common.COST_NONE_OR_MULTIPLE_PROJECTS;
		}
		return Common.COST_NONE_OR_MULTIPLE_PROJECTS * projects.size();
	}

	public String toString() {
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

		return firstName + " " + lastName + buf + id + buf + stream + buf + projects;
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

	public Double getGPA() {
		return gpa;
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
		this.projects.set(index, project);
		return this;
	}

	public Student setPreferenceList(ArrayList<Project> preferenceList) {
		this.preferenceList = preferenceList;
		return this;
	}

	public Student getCopy() {
		return new Student(firstName, lastName, id, stream, gpa, projects.get(0), preferenceList);
	}
}
