package ie.ucd.interfaces;

import ie.ucd.objects.Project;

public interface StaffMemberInterface {
	public Boolean isAllActivitiesUsed();

	public Project getProject();

	public String getProposedBy();

	public String[] getResearchActivities();

	public String[] getResearchAreas();

	public String getStream();

	public void setProposedBy(String proposedBy);

	public void setResearchActivities(String[] researchActivities);

	public void setResearchAreas(String[] researchAreas);

	public void setStream(String stream);
}
