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
		dummyCandidateSolution = new CandidateSolution(dummyNumStudents, dummyParser.allStaffsProjects,
				dummyParser.allNames, null, null);
	}

	@Test
	@DisplayName("Test initialization")
	public void testInitialization() {
		System.out.println("Running: testInitialization");
		Assert.assertEquals(dummyParser.allStaffsProjects, dummyCandidateSolution.getStaffMembers());
		Assert.assertEquals(dummyParser.allNames, dummyCandidateSolution.getNames());
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

	@Test
	@DisplayName("Test calculateGlobalSatisfaction and calculateEnergy")
	public void testCalculateGlobalSatisfactionAndCalculateEnergy() throws IOException {
		System.out.println("Running: testCalculateGlobalSatisfactionAndCalculateEnergy");
		dummyCandidateSolution.generateProjects();
		dummyCandidateSolution.generateStudents();

		double currSatisfaction = dummyCandidateSolution.calculateGlobalSatisfaction();
		double currEnergy = dummyCandidateSolution.calculateEnergy();

		dummyCandidateSolution.makeRandomChange();
		double nextSatisfaction = dummyCandidateSolution.calculateGlobalSatisfaction();
		double nextEnergy = dummyCandidateSolution.calculateEnergy();
		// System.out.println(currEnergy + " " + nextEnergy + " " + currSatisfaction + " " + nextSatisfaction);

		Assert.assertTrue((currEnergy > nextEnergy && currSatisfaction < nextSatisfaction)
				|| (currEnergy < nextEnergy && currSatisfaction > nextSatisfaction));
	}

	@AfterEach
	protected void tearDown() throws Exception {
		System.out.println("Running: tearDown");
		dummyCandidateSolution = null;
		Assert.assertNull(dummyCandidateSolution);
	}
}
