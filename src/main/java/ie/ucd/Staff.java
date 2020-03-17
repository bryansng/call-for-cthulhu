package ie.ucd;

import java.util.Random;

public class Staff {
	public String proposedBy;
	public String[] researchActivities;
	private boolean[] researchActivitiesUsed;
	private int numActivitiesUsed = 0;
	public String[] researchAreas;
	public String specialFocus;

	public Staff(String proposedBy, String researchActivities, String researchAreas, String specialFocus) {
		this.proposedBy = proposedBy;
		this.researchActivities = researchActivities.split(", ");
		this.researchActivitiesUsed = new boolean[researchActivities.length()]; // are they initialized to false?
		this.researchAreas = researchAreas.split(", ");
		this.specialFocus = parseSpecialFocus(specialFocus);
	}

	private String parseSpecialFocus(String specialFocus) {
		if (specialFocus.equals("Dagon Studies")) {
			return "DS";
		}
		String[] streams = new String[] { "CS", "CS+DS" };
		int choice = new Random().nextInt(2); // between 0 and 1.
		return streams[choice];
	}

	public Boolean isAllActivitiesUsed() {
		return numActivitiesUsed == researchActivities.length;
	}

	public SupervisorProject getSupervisorProject() {
		return new SupervisorProject(proposedBy, getRandomUnusedResearchActivity(), specialFocus);
	}

	// need check if isAllActivitiesUsed, if not, this will loop indefinitely.
	private String getRandomUnusedResearchActivity() {
		int randInt = new Random().nextInt(researchActivities.length);
		while (researchActivitiesUsed[randInt] == true) {
			randInt = new Random().nextInt(researchActivities.length);
		}
		researchActivitiesUsed[randInt] = true;
		numActivitiesUsed += 1;
		return researchActivities[randInt];
	}

	public String toString() {
		String buf = " - ";
		return researchActivities + buf + specialFocus;
	}
}