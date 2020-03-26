package ie.ucd.objects;

import java.util.Random;

import ie.ucd.Settings;

public class StaffMember {
	private String proposedBy;
	private String[] researchActivities;
	private boolean[] researchActivitiesUsed;
	private int numActivitiesUsed = 0;
	private String[] researchAreas;
	private String stream;

	public StaffMember(String proposedBy, String researchActivities, String researchAreas, String stream) {
		this.proposedBy = proposedBy;
		this.researchActivities = researchActivities.trim().split("\\s*,\\s*");
		this.researchActivitiesUsed = new boolean[this.researchActivities.length]; // are they initialized to false?
		this.researchAreas = researchAreas.trim().split("\\s*,\\s*");
		this.stream = parseStream(stream);
	}

	private String parseStream(String stream) {
		if (stream.equals("Dagon Studies")) {
			return "DS";
		}
		String[] streams = new String[] { "CS", "CS+DS" };
		int choice = new Random().nextInt(2); // between 0 and 1.
		return streams[choice];
	}

	public Boolean isAllActivitiesUsed() {
		return numActivitiesUsed == researchActivities.length;
	}

	public Project getProject() {
		return new Project(proposedBy, getRandomUnusedResearchActivity(), stream, Settings.getProbability());
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
		String string = proposedBy + buf;
		for (String researchActivity : researchActivities) string += researchActivity + buf;
		string += stream;
		return string;
	}

	public String getProposedBy() {
		return proposedBy;
	}

	public String[] getResearchActivities() {
		return researchActivities;
	}

	public String[] getResearchAreas() {
		return researchAreas;
	}

	public String getStream() {
		return stream;
	}

	public void setProposedBy(String proposedBy) {
		this.proposedBy = proposedBy;
	}

	public void setResearchActivities(String[] researchActivities) {
		this.researchActivities = researchActivities;
	}

	public void setResearchAreas(String[] researchAreas) {
		this.researchAreas = researchAreas;
	}

	public void setStream(String stream) {
		this.stream = stream;
	}
}
