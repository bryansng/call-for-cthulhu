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
		this.firstName = firstName;
		this.lastName = lastName;
		this.id = id;
		this.stream = stream;
		this.gpa = gpa;
		this.projects.add(preferenceList.get(0));
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

	public ArrayList<Project> getPreferenceList() {
		return preferenceList;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setStream(String stream) {
		this.stream = stream;
	}

	public void setGPA(Double gpa) {
		this.gpa = gpa;
	}

	public void setProjectsAssigned(ArrayList<Project> projects) {
		this.projects = projects;
	}

	public void setPreferenceList(ArrayList<Project> preferenceList) {
		this.preferenceList = preferenceList;
	}
}
