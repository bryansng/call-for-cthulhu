package ie.ucd.interfaces;

import java.util.ArrayList;
import ie.ucd.objects.Project;

public interface StudentInterface {
	public Boolean isGiven1stPreference();

	public String getFirstName();

	public String getLastName();

	public Integer getId();

	public String getStream();

	public Project getProjectAssigned();

	public ArrayList<Project> getPreferenceList();

	public void setFirstName(String firstName);

	public void setLastName(String lastName);

	public void setId(Integer id);

	public void setStream(String stream);

	public void setProjectAssigned(Project projectAssigned);

	public void setPreferenceList(ArrayList<Project> preferenceList);
}
