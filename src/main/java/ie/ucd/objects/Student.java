package ie.ucd.objects;

import java.util.ArrayList;
import ie.ucd.interfaces.StudentInterface;

public class Student implements StudentInterface {
	private String firstName;
	private String lastName;
	private Integer id;
	private String stream; // CS or DS.
	private Double gpa;
	private Project projectAssigned;
	private ArrayList<Project> preferenceList;

	public Student(String firstName, String lastName, Integer id, String stream, Double gpa,
			ArrayList<Project> preferenceList) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.id = id;
		this.stream = stream;
		this.gpa = gpa;
		this.projectAssigned = preferenceList.get(0);
		this.preferenceList = preferenceList;
	}

	// in theory, if java stores by reference, their references should be the same, so can check via == if they memory location is the same.
	public Boolean isGiven1stPreference() {
		// return projectAssigned.equals(preferenceList.get(0));	// if use this, need to implement .equals() method in Project.java.
		return projectAssigned == preferenceList.get(0);
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

	public Project getProjectAssigned() {
		return projectAssigned;
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

	public void setProjectAssigned(Project projectAssigned) {
		this.projectAssigned = projectAssigned;
	}

	public void setPreferenceList(ArrayList<Project> preferenceList) {
		this.preferenceList = preferenceList;
	}
}
