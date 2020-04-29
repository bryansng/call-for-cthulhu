package ie.ucd.objects;

import java.util.Random;
import ie.ucd.Common;
import ie.ucd.interfaces.StaffMemberInterface;

public class StaffMember implements StaffMemberInterface {
	private String proposedBy;
	private String[] researchActivities;
	private boolean[] researchActivitiesUsed;
	private int numActivitiesUsed;
	private String[] researchAreas;
	private String stream;

	public StaffMember(String proposedBy, String researchActivities, String researchAreas, String stream) {
		this.proposedBy = proposedBy;
		this.researchActivities = researchActivities.trim().split("\\s*,\\s*");
		resetResearchActivitiesUsed();
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
		return new Project(this, getRandomUnusedResearchActivity(), stream, Common.getProbability());
	}

	public int getNumberActivitiesUsed() {
		return numActivitiesUsed;
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

	public void setProjectUnused(String researchActivity) {
		for (int i = 0; i < researchActivities.length; i++) {
			if (researchActivity.equals(researchActivities[i])) {
				researchActivitiesUsed[i] = false;
				numActivitiesUsed -= 1;
				break;
			}
		}
	}

	public String toString() {
		return proposedBy;
	}

	public String toStringVerbose() {
		String buf = " - ";
		String string = proposedBy + buf;
		for (String researchActivity : researchActivities)
			string += researchActivity + ",";
		string += buf + stream;
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

	public void resetResearchActivitiesUsed() {
		researchActivitiesUsed = new boolean[this.researchActivities.length];
		numActivitiesUsed = 0;
	}
}
