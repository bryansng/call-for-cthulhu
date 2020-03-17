package ie.ucd.objects;

import java.util.ArrayList;

public class Student {
	public String firstName;
	public String lastName;
	public Integer id;
	public String stream; // CS or DS.
	public ArrayList<SupervisorProject> preferenceList;

	public Student(String firstName, String lastName, Integer id, String stream,
			ArrayList<SupervisorProject> preferenceList) {
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
		for (SupervisorProject sp : preferenceList) {
			projects += sp.researchActivity;
			count++;

			if (count < preferenceList.size()) {
				projects += ", ";
			}
		}

		return firstName + " " + lastName + buf + id + buf + stream + buf + projects;
	}
}