package ie.ucd.interfaces;

public interface ProjectInterface {
	public Boolean hasCompatibleStream(String studentStream);

	public Boolean doesStudentPreferProject();

	public void incrementStudentsAssigned();

	public void incrementTimesAs1stPreference();

	public String getProposedBy();

	public String getResearchActivity();

	public String getStream();

	public Boolean getIsGivenAs1stPreference();

	public Double getPreferredProbability();

	public Double getNumStudentsAssigned();

	public Double getNumTimesAsStudents1stPreference();

	public void setProposedBy(String proposedBy);

	public void setResearchActivity(String researchActivity);

	public void setStream(String stream);

	public void setIsGivenAs1stPreference(Boolean isGivenAs1stPreference);
}
