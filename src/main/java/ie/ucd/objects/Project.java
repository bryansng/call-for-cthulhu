package ie.ucd.objects;

import ie.ucd.Common;
import ie.ucd.Settings;
import ie.ucd.interfaces.ProjectInterface;
import ie.ucd.interfaces.SearchMatchable;

public class Project implements ProjectInterface, SearchMatchable {
	private StaffMember staffMember;
	private String researchActivity;
	private String stream;
	private boolean isGivenAs1stPreference = false;
	private double preferredProbability;
	private double numAsPreference = 0.0;
	private double numAs1stPreference = 0.0;
	private double numStudentsAssigned = 0.0;

	// violations.
	private boolean assignmentViolation;

	public Project(StaffMember staffMember, String researchActivity, String stream, double preferredProbability) {
		this.staffMember = staffMember;
		this.researchActivity = researchActivity;
		this.stream = stream;
		this.preferredProbability = preferredProbability;
	}

	public Double calculateSatisfaction() {
		assignmentViolation = false;
		return Settings.enableProjectAssignedToOneStudent ? assignmentSatisfaction() : 0.0;
	}

	// hard: project assigned to at most one student.
	private Double assignmentSatisfaction() {
		if (Common.DEBUG_SHOW_PROJECT_NUM_STUDENT_ASSIGNED) {
			if (numStudentsAssigned < 0)
				System.out.println("is less than zero, this is not expected. is " + numStudentsAssigned);
			if (numStudentsAssigned > 60) {
				System.out.println("too big is " + numStudentsAssigned);
			}
		}
		if (numStudentsAssigned == 1.0 || numStudentsAssigned == 0.0) {
			return -Common.COST_NONE_OR_MULTIPLE_PROJECTS;
		}
		assignmentViolation = true;
		return Common.COST_NONE_OR_MULTIPLE_PROJECTS * numStudentsAssigned;
	}

	public Boolean hasCompatibleStream(String studentStream) {
		// CS == CS or DS == DS
		if (stream.equals(studentStream) || (stream.equals("CS+DS") && studentStream.equals("CS"))) {
			return true;
		}
		return false;
	}

	public Boolean doesStudentPreferProject() {
		double randomProb = Common.getMin() + (Common.getRange() * Common.rand.nextDouble());
		if (randomProb <= preferredProbability) {
			return true;
		}
		return false;
	}

	public boolean matchSearchTerm(String searchTerm) {
		searchTerm = searchTerm.toLowerCase();
		try {
			return staffMember.getProposedBy().toLowerCase().contains(searchTerm)
					|| researchActivity.toLowerCase().contains(searchTerm) || stream.toLowerCase().equals(searchTerm);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void incrementAsPreference() {
		numAsPreference += 1.0;
	}

	public void incrementAs1stPreference() {
		numAs1stPreference += 1.0;
	}

	public void decrementAs1stPreference() {
		if (numAs1stPreference > 0)
			numAs1stPreference -= 1.0;
	}

	public void incrementStudentsAssigned() {
		numStudentsAssigned += 1.0;
	}

	public void decrementStudentsAssigned() {
		if (numStudentsAssigned > 0)
			numStudentsAssigned -= 1.0;
	}

	public String toString() {
		return researchActivity;
	}

	public String toStringVerbose() {
		String buf = "\t";
		return staffMember.getProposedBy() + buf + researchActivity + buf + stream + buf + preferredProbability;
	}

	public StaffMember getStaffMember() {
		return staffMember;
	}

	public String getResearchActivity() {
		return researchActivity;
	}

	public String getStream() {
		return stream;
	}

	public Boolean isGivenAs1stPreference() {
		return isGivenAs1stPreference;
	}

	public Double getPreferredProbability() {
		return preferredProbability;
	}

	public Double getNumAsPreference() {
		return numAsPreference;
	}

	public Double getNumAs1stPreference() {
		return numAs1stPreference;
	}

	public Double getNumStudentsAssigned() {
		return numStudentsAssigned;
	}

	public boolean isAssignmentViolation() {
		return assignmentViolation;
	}

	public void setStaffMember(StaffMember staffMember) {
		this.staffMember = staffMember;
	}

	public void setResearchActivity(String researchActivity) {
		this.researchActivity = researchActivity;
	}

	public void setStream(String stream) {
		this.stream = stream;
	}

	public void setNumStudentsAssigned(double numStudentsAssigned) {
		this.numStudentsAssigned = numStudentsAssigned;
	}

	public void setIsGivenAs1stPreference(Boolean isGivenAs1stPreference) {
		this.isGivenAs1stPreference = isGivenAs1stPreference;
	}

	public void resetCounters() {
		numAsPreference = 0;
		numAs1stPreference = 0;
		numStudentsAssigned = 0;
		isGivenAs1stPreference = false;
	}

	public void resetNumStudentsAssigned() {
		numStudentsAssigned = 0;
	}
}
