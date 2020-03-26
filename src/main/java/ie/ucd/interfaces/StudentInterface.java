package ie.ucd.interfaces;

import java.util.ArrayList;
import ie.ucd.objects.Project;
import ie.ucd.objects.Student;

public interface StudentInterface {
	public String getFirstName();

	public String getLastName();

	public Integer getId();

	public String getStream();

	public ArrayList<Project> getProjectAssigned();

	public ArrayList<Project> getPreferenceList();

	public Student setFirstName(String firstName);

	public Student setLastName(String lastName);

	public Student setId(Integer id);

	public Student setStream(String stream);

	public Student setProjectsAssigned(ArrayList<Project> projectAssigned);

	public Student setPreferenceList(ArrayList<Project> preferenceList);
}
