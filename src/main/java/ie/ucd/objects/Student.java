package ie.ucd.objects;

import java.util.ArrayList;
import ie.ucd.interfaces.StudentInterface;

public class Student implements StudentInterface {
	public String firstName;
	public String lastName;
	public Integer id;
	public String stream; // CS or DS.
	public ArrayList<Project> preferenceList;

	public Student(String firstName, String lastName, Integer id, String stream, ArrayList<Project> preferenceList) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.id = id;
		this.stream = stream;
		this.preferenceList = preferenceList;
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

	public void setPreferenceList(ArrayList<Project> preferenceList) {
		this.preferenceList = preferenceList;
	}
}
