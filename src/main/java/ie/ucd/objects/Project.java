package ie.ucd.objects;

import ie.ucd.Common;
import ie.ucd.interfaces.ProjectInterface;

public class Project implements ProjectInterface {
	private String proposedBy;
	private String researchActivity;
	private String stream;
	private boolean isGivenAs1stPreference = false;
	private double preferredProbability;
	private double numStudentsAssigned = 0.0;
	private double numTimesAsStudents1stPreference = 0.0;

	public Project(String proposedBy, String researchActivity, String stream, double preferredProbability) {
		this.proposedBy = proposedBy;
		this.researchActivity = researchActivity;
		this.stream = stream;
		this.preferredProbability = preferredProbability;
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

	public void incrementStudentsAssigned() {
		numStudentsAssigned += 1.0;
	}

	public void incrementTimesAs1stPreference() {
		numTimesAsStudents1stPreference += 1.0;
	}

	public String toString() {
		String buf = "\t\t";
		return proposedBy + buf + researchActivity + buf + stream + buf + preferredProbability;
	}

	public String getProposedBy() {
		return proposedBy;
	}

	public String getResearchActivity() {
		return researchActivity;
	}

	public String getStream() {
		return stream;
	}

	public Boolean getIsGivenAs1stPreference() {
		return isGivenAs1stPreference;
	}

	public Double getPreferredProbability() {
		return preferredProbability;
	}

	public Double getNumStudentsAssigned() {
		return numStudentsAssigned;
	}

	public Double getNumTimesAsStudents1stPreference() {
		return numTimesAsStudents1stPreference;
	}

	public void setProposedBy(String proposedBy) {
		this.proposedBy = proposedBy;
	}

	public void setResearchActivity(String researchActivity) {
		this.researchActivity = researchActivity;
	}

	public void setStream(String stream) {
		this.stream = stream;
	}

	public void setIsGivenAs1stPreference(Boolean isGivenAs1stPreference) {
		this.isGivenAs1stPreference = isGivenAs1stPreference;
	}
}
