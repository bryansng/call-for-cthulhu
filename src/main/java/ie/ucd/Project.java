package ie.ucd;

public class Project {
	public String proposedBy;
	public String researchActivity;
	public String[] researchAreas;
	public String specialFocus;

	public Project(String proposedBy, String researchActivity, String researchAreas, String specialFocus) {
		this.proposedBy = proposedBy;
		this.researchActivity = researchActivity;
		this.researchAreas = researchAreas.split(", ");
		this.specialFocus = parseSpecialFocus(specialFocus);
	}

	private String parseSpecialFocus(String specialFocus) {
		if (specialFocus.equals("Dagon Studies")) {
			return "DS";
		}
		return "CS+DS";
	}

	public String toString() {
		String buf = " - ";
		return researchActivity + buf + specialFocus;
	}
}