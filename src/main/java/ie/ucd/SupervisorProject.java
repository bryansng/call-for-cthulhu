package ie.ucd;

public class SupervisorProject {
	public String proposedBy;
	public String researchActivity;
	public String specialFocus;

	public SupervisorProject(String proposedBy, String researchActivity, String specialFocus) {
		this.proposedBy = proposedBy;
		this.researchActivity = researchActivity;
		this.specialFocus = specialFocus;
	}

	public String toString() {
		String buf = "\t\t";
		return proposedBy + buf + researchActivity + buf + specialFocus;
	}
}