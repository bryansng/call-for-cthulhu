package ie.ucd;

import java.util.Random;

public class StaffMember {
	public String researchActivity;
	public String specialFocus;

	public StaffMember(String researchActivity, String specialFocus) {
		this.researchActivity = researchActivity;
		this.specialFocus = parseSpecialFocus(specialFocus);
	}

	private String parseSpecialFocus(String specialFocus) {
		if (specialFocus.equals("Dagon Studies")) {
			return "DS";
		}
		String[] streams = new String[] { "CS", "CS+DS" };
		int choice = new Random().nextInt(2); // between 0 and 1.
		return streams[choice];
	}

	public String toString() {
		String buf = " - ";
		return researchActivity + buf + specialFocus;
	}
}