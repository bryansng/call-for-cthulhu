package ie.ucd.objects;

public class SupervisorProject {
	public String proposedBy;
	public String researchActivity;
	public String specialFocus;
	public double numStudentsAssigned = 0.0;
	public double numTimesAsStudents1stPreference = 0.0;

	public SupervisorProject(String proposedBy, String researchActivity, String specialFocus) {
		this.proposedBy = proposedBy;
		this.researchActivity = researchActivity;
		this.specialFocus = specialFocus;
	}

	public Boolean hasCompatibleStream(String studentStream) {
		// CS == CS or DS == DS
		if (specialFocus.equals(studentStream) || (specialFocus.equals("CS+DS") && studentStream.equals("CS"))) {
			return true;
		}
		return false;
	}

	public void incrementStudentsAssigned() {
		numStudentsAssigned += 1.0;
	}

	public void incrementTimesAs1stPreference() {
		numTimesAsStudents1stPreference += 1.0;
	}

	public String toString() {
		String buf = "\t\t";
		return proposedBy + buf + researchActivity + buf + specialFocus;
	}
}