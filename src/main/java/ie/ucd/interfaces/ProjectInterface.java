package ie.ucd.interfaces;

import ie.ucd.objects.StaffMember;

public interface ProjectInterface {
	public Boolean hasCompatibleStream(String studentStream);

	public Boolean doesStudentPreferProject();

	public void incrementAsPreference();

	public void incrementAs1stPreference();

	public StaffMember getStaffMember();

	public String getResearchActivity();

	public String getStream();

	public Boolean isGivenAs1stPreference();

	public Double getPreferredProbability();

	public Double getNumAsPreference();

	public Double getNumAs1stPreference();

	public void setStaffMember(StaffMember proposedBy);

	public void setResearchActivity(String researchActivity);

	public void setStream(String stream);

	public void setIsGivenAs1stPreference(Boolean isGivenAs1stPreference);
}
