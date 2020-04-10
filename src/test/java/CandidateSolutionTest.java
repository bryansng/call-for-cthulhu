import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import ie.ucd.Common;
import ie.ucd.io.Parser;
import ie.ucd.objects.CandidateSolution;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import org.junit.Assert;

public class CandidateSolutionTest {
	private int dummyNumStudents;
	private Parser dummyParser;
	private CandidateSolution dummyCandidateSolution;

	@BeforeEach
	protected void setUp() throws Exception {
		System.out.println("Setting CandidateSolutionTest up!");
		dummyNumStudents = 500;
		dummyParser = new Parser();
		dummyCandidateSolution = new CandidateSolution(dummyNumStudents, dummyParser.getStaffMembers(),
				dummyParser.getNames(), null, null);
	}

	@Test
	@DisplayName("Test initialization")
	public void testInitialization() {
		System.out.println("Running: testInitialization");
		Assert.assertEquals(dummyParser.getStaffMembers(), dummyCandidateSolution.getStaffMembers());
		Assert.assertEquals(dummyParser.getNames(), dummyCandidateSolution.getNames());
		Assert.assertEquals(null, dummyCandidateSolution.getProjects());
		Assert.assertEquals(null, dummyCandidateSolution.getStudents());
	}

	@Test
	@DisplayName("Test generateProjects")
	public void testGenerateProjects() throws IOException {
		System.out.println("Running: testGenerateProjects");
		Assert.assertEquals(dummyNumStudents * Common.numAvgProjectsProposed / 2,
				dummyCandidateSolution.generateProjects().size());
	}

	@Test
	@DisplayName("Test generateStudents")
	public void testGenerateStudents() throws IOException {
		System.out.println("Running: testGenerateStudents");
		dummyCandidateSolution.generateProjects();
		Assert.assertEquals(dummyNumStudents, dummyCandidateSolution.generateStudents().size());
	}

	@AfterEach
	protected void tearDown() throws Exception {
		System.out.println("Running: tearDown");
		dummyCandidateSolution = null;
		Assert.assertNull(dummyCandidateSolution);
	}
}
