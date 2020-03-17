package ie.ucd;

import java.util.ArrayList;
import java.util.Random;

public class Student {
	public String firstName;
	public String lastName;
	public Integer id;
	public String stream; // CS or DS.
	public ArrayList<SupervisorProject> preferenceList;

	public Student(String firstName, String lastName, Integer id, ArrayList<SupervisorProject> preferenceList) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.id = id;
		this.stream = assignStream();
		this.preferenceList = preferenceList;
	}

	private String assignStream() {
		int randomInt = new Random().nextInt(5) + 1; // 1-5

		// 40% is DS, 2/5.
		if (randomInt == 4 || randomInt == 5) {
			return "DS";
		}
		return "CS"; // 60% is CS, 3/5.
	}
}