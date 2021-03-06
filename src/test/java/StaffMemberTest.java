import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import ie.ucd.objects.StaffMember;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.HashSet;
import org.junit.Assert;

public class StaffMemberTest {
	private StaffMember dummyStaffMember;

	@BeforeEach
	protected void setUp() throws Exception {
		System.out.println("Setting StaffMemberTest up!");
		dummyStaffMember = new StaffMember("Wolfgang Amadeus Mozart", "composing classical music, playing the piano",
				"Classical music", "");
	}

	@Test
	@DisplayName("Test initialization")
	public void testInitialization() {
		System.out.println("Running: testInitialization");
		HashSet<String> possibleStreams = new HashSet<String>();
		possibleStreams.add("CS");
		possibleStreams.add("CS+DS");

		Assert.assertEquals("Wolfgang Amadeus Mozart", dummyStaffMember.getProposedBy());
		Assert.assertEquals(2, dummyStaffMember.getResearchActivities().length);
		Assert.assertTrue(possibleStreams.contains(dummyStaffMember.getStream()));
		Assert.assertEquals(1, dummyStaffMember.getResearchAreas().length);
	}

	@AfterEach
	protected void tearDown() throws Exception {
		System.out.println("Running: tearDown");
		dummyStaffMember = null;
		Assert.assertNull(dummyStaffMember);
	}
}
