import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import ie.ucd.Common;
import ie.ucd.objects.Project;
import ie.ucd.objects.StaffMember;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.Assert;

public class ProjectTest {
	private Project dummyProject;
	private StaffMember dummyStaffMember;
	private Double dummyPreferredProbability;

	@BeforeEach
	protected void setUp() throws Exception {
		System.out.println("Setting ProjectTest up!");
		dummyStaffMember = new StaffMember("Wolfgang Amadeus Mozart", "composing classical music, playing the piano",
				"Classical music", "CS+DS");
		dummyPreferredProbability = Common.getProbability();
		dummyProject = new Project(dummyStaffMember, "playing the piano", "CS+DS", dummyPreferredProbability);
	}

	@Test
	@DisplayName("Test initialization")
	public void testInitialization() {
		System.out.println("Running: testInitialization");
		Assert.assertEquals(dummyStaffMember, dummyProject.getStaffMember());
		Assert.assertEquals("playing the piano", dummyProject.getResearchActivity());
		Assert.assertEquals("CS+DS", dummyProject.getStream());
		Assert.assertEquals(dummyPreferredProbability, dummyProject.getPreferredProbability());
	}

	@Test
	@DisplayName("Test hasCompatibleStream")
	public void testHasCompatibleStream() {
		System.out.println("Running: testHasCompatibleStream");
		Assert.assertEquals(Boolean.TRUE, dummyProject.hasCompatibleStream("CS"));
		Assert.assertEquals(Boolean.TRUE, dummyProject.hasCompatibleStream("CS"));
		Assert.assertEquals(Boolean.FALSE, dummyProject.hasCompatibleStream("DS"));
	}

	@AfterEach
	protected void tearDown() throws Exception {
		System.out.println("Running: tearDown");
		dummyProject = null;
		Assert.assertNull(dummyProject);
	}
}
