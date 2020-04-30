import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import ie.ucd.io.Parser;
import ie.ucd.objects.CandidateSolution;
import ie.ucd.solvers.SimulatedAnnealing;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.IOException;

import org.apache.commons.lang3.mutable.MutableInt;
import org.junit.Assert;

public class SimulatedAnnealingTest {
	private MutableInt dummyNumStudents;
	private Parser dummyParser;
	private CandidateSolution dummyCandidateSolution;

	@BeforeEach
	protected void setUp() throws Exception {
		System.out.println("Setting SimulatedAnnealingTest up!");
		dummyNumStudents = new MutableInt(500);
		dummyParser = new Parser();
		dummyCandidateSolution = new CandidateSolution(dummyNumStudents, dummyParser.getStaffMembers(),
				dummyParser.getNames(), null, null);
	}

	@Test
	@DisplayName("Test calculateGlobalSatisfaction and calculateEnergy")
	public void testCalculateGlobalSatisfactionAndCalculateEnergy() throws IOException {
		System.out.println("Running: testCalculateGlobalSatisfactionAndCalculateEnergy");
		dummyCandidateSolution.generateProjects();
		try {
			dummyCandidateSolution.generateStudents();
		} catch (Exception e) {
		}
		SimulatedAnnealing solverSA = new SimulatedAnnealing(dummyCandidateSolution);

		// calculate satisfaction and energy of curr solution.
		double currSatisfaction = dummyCandidateSolution.calculateGlobalSatisfaction();
		double currEnergy = solverSA.calculateEnergy(dummyCandidateSolution);

		// make random change to solution.
		CandidateSolution newCandidateSolution = solverSA.makeRandomMoveV2(dummyCandidateSolution);

		// calculate satisfaction and energy of new solution.
		double nextSatisfaction = newCandidateSolution.calculateGlobalSatisfaction();
		double nextEnergy = solverSA.calculateEnergy(newCandidateSolution);

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
